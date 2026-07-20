package gov.lawenforcement.graph.service;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class GraphPopulatorService {

    private final JdbcTemplate jdbc;
    private final Driver neo4jDriver;

    public GraphPopulatorService(JdbcTemplate jdbc, Driver neo4jDriver) {
        this.jdbc = jdbc;
        this.neo4jDriver = neo4jDriver;
    }

    public void populateAll() {
        log.info("Starting full graph population from PostgreSQL...");
        long start = System.currentTimeMillis();

        try (Session session = neo4jDriver.session()) {
            clearGraph(session);
            mergeCaseNodes(session);
            mergePersonNodes(session);
            mergeAccusedOfRelationships(session);
            mergeVictimOfRelationships(session);
            mergeComplaintOfRelationships(session);
            mergeCoOffenderRelationships(session);
            mergeArrestedInRelationships(session);
        }

        long elapsed = System.currentTimeMillis() - start;
        log.info("Graph population completed in {}ms", elapsed);
    }

    private void clearGraph(Session session) {
        log.info("Clearing existing graph data...");
        session.executeWrite(tx -> {
            tx.run("MATCH (n) DETACH DELETE n");
            return null;
        });
    }

    private void mergeCaseNodes(Session session) {
        log.info("Merging Case nodes...");
        String sql = """
            SELECT c.crime_no, c.case_no, c.brief_facts, c.crime_registered_date,
                   c.latitude, c.longitude,
                   cs.case_status_name,
                   d.district_name,
                   ch.crime_group_name
            FROM case_master c
            LEFT JOIN case_status_master cs ON c.case_status_id = cs.case_status_id
            LEFT JOIN district d ON c.police_station_id = d.district_id
            LEFT JOIN crime_head ch ON c.crime_major_head_id = ch.crime_head_id
            WHERE c.crime_no IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        log.info("Found {} cases in PostgreSQL", rows.size());

        int batchSize = 500;
        for (int i = 0; i < rows.size(); i += batchSize) {
            List<Map<String, Object>> batch = rows.subList(i, Math.min(i + batchSize, rows.size()));
            session.executeWrite(tx -> {
                for (Map<String, Object> row : batch) {
                    String crimeNo = Objects.toString(row.get("crime_no"), null);
                    if (crimeNo == null) continue;

                    Map<String, Object> params = new HashMap<>();
                    params.put("crimeNo", crimeNo);
                    params.put("caseNo", row.get("case_no"));
                    params.put("briefFacts", row.get("brief_facts"));
                    params.put("crimeRegisteredDate", toNeo4jString(row.get("crime_registered_date")));
                    params.put("latitude", toNeo4jDouble(row.get("latitude")));
                    params.put("longitude", toNeo4jDouble(row.get("longitude")));
                    params.put("statusName", row.get("case_status_name"));
                    params.put("districtName", row.get("district_name"));
                    params.put("crimeHeadName", row.get("crime_group_name"));

                    tx.run("""
                        MERGE (c:Case {crimeNo: $crimeNo})
                        SET c.caseNo = $caseNo,
                            c.briefFacts = $briefFacts,
                            c.crimeRegisteredDate = $crimeRegisteredDate,
                            c.latitude = $latitude,
                            c.longitude = $longitude,
                            c.statusName = $statusName,
                            c.districtName = $districtName,
                            c.crimeHeadName = $crimeHeadName
                        """, params);
                }
                return null;
            });
        }
        log.info("Merged {} Case nodes", rows.size());
    }

    private void mergePersonNodes(Session session) {
        mergeAccusedPersons(session);
        mergeVictimPersons(session);
        mergeComplainantPersons(session);
    }

    private void mergeAccusedPersons(Session session) {
        log.info("Merging Accused Person nodes...");
        String sql = """
            SELECT DISTINCT a.person_id, a.accused_name, a.age_year, a.gender_id
            FROM accused a
            WHERE a.person_id IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        mergePersonBatch(session, rows, "ACCUSED");
        log.info("Merged {} Accused Person nodes", rows.size());
    }

    private void mergeVictimPersons(Session session) {
        log.info("Merging Victim Person nodes...");
        String sql = """
            SELECT DISTINCT 'VICTIM_' || v.victim_master_id AS person_id,
                   v.victim_name, v.age_year, v.gender_id
            FROM victim v
            WHERE v.victim_name IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        mergePersonBatch(session, rows, "VICTIM");
        log.info("Merged {} Victim Person nodes", rows.size());
    }

    private void mergeComplainantPersons(Session session) {
        log.info("Merging Complainant Person nodes...");
        String sql = """
            SELECT DISTINCT 'COMP_' || c.complainant_id AS person_id,
                   c.complainant_name, c.age_year, c.gender_id
            FROM complainant_details c
            WHERE c.complainant_name IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        mergePersonBatch(session, rows, "COMPLAINANT");
        log.info("Merged {} Complainant Person nodes", rows.size());
    }

    private void mergePersonBatch(Session session, List<Map<String, Object>> rows, String personType) {
        int batchSize = 500;
        for (int i = 0; i < rows.size(); i += batchSize) {
            List<Map<String, Object>> batch = rows.subList(i, Math.min(i + batchSize, rows.size()));
            session.executeWrite(tx -> {
                for (Map<String, Object> row : batch) {
                    String personId = Objects.toString(row.get("person_id"), null);
                    if (personId == null) continue;

                    Map<String, Object> params = new HashMap<>();
                    params.put("personId", personId);
                    params.put("name", row.get("accused_name") != null
                            ? row.get("accused_name")
                            : row.get("victim_name") != null
                                    ? row.get("victim_name")
                                    : row.get("complainant_name"));
                    params.put("age", row.get("age_year"));
                    params.put("gender", row.get("gender_id"));
                    params.put("personType", personType);

                    tx.run("""
                        MERGE (p:Person {personId: $personId})
                        SET p.name = $name,
                            p.age = $age,
                            p.gender = $gender,
                            p.personType = $personType
                        """, params);
                }
                return null;
            });
        }
    }

    private void mergeAccusedOfRelationships(Session session) {
        log.info("Merging ACCUSED_OF relationships...");
        String sql = """
            SELECT a.person_id, c.crime_no
            FROM accused a
            JOIN case_master c ON a.case_master_id = c.case_master_id
            WHERE a.person_id IS NOT NULL AND c.crime_no IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        mergeCaseRelationshipBatch(session, rows, "ACCUSED_OF", "caseCrimeNo");
        log.info("Merged {} ACCUSED_OF relationships", rows.size());
    }

    private void mergeVictimOfRelationships(Session session) {
        log.info("Merging VICTIM_OF relationships...");
        String sql = """
            SELECT 'VICTIM_' || v.victim_master_id AS person_id, c.crime_no
            FROM victim v
            JOIN case_master c ON v.case_master_id = c.case_master_id
            WHERE v.victim_name IS NOT NULL AND c.crime_no IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        mergeCaseRelationshipBatch(session, rows, "VICTIM_OF", "caseCrimeNo");
        log.info("Merged {} VICTIM_OF relationships", rows.size());
    }

    private void mergeComplaintOfRelationships(Session session) {
        log.info("Merging COMPLAINT_OF relationships...");
        String sql = """
            SELECT 'COMP_' || cd.complainant_id AS person_id, c.crime_no
            FROM complainant_details cd
            JOIN case_master c ON cd.case_master_id = c.case_master_id
            WHERE cd.complainant_name IS NOT NULL AND c.crime_no IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        mergeCaseRelationshipBatch(session, rows, "COMPLAINT_OF", "caseCrimeNo");
        log.info("Merged {} COMPLAINT_OF relationships", rows.size());
    }

    private void mergeCaseRelationshipBatch(Session session, List<Map<String, Object>> rows,
                                             String relType, String propName) {
        int batchSize = 500;
        for (int i = 0; i < rows.size(); i += batchSize) {
            List<Map<String, Object>> batch = rows.subList(i, Math.min(i + batchSize, rows.size()));
            session.executeWrite(tx -> {
                for (Map<String, Object> row : batch) {
                    String personId = Objects.toString(row.get("person_id"), null);
                    String crimeNo = Objects.toString(row.get("crime_no"), null);
                    if (personId == null || crimeNo == null) continue;

                    String cypher = String.format("""
                        MATCH (p:Person {personId: $personId})
                        MATCH (c:Case {crimeNo: $crimeNo})
                        MERGE (p)-[r:%s]->(c)
                        SET r.%s = $crimeNo
                        """, relType, propName);

                    Map<String, Object> params = Map.of("personId", personId, "crimeNo", crimeNo);
                    tx.run(cypher, params);
                }
                return null;
            });
        }
    }

    private void mergeCoOffenderRelationships(Session session) {
        log.info("Merging CO_OFFENDER relationships...");
        String sql = """
            SELECT DISTINCT a1.person_id AS person1, a2.person_id AS person2
            FROM accused a1
            JOIN accused a2 ON a1.case_master_id = a2.case_master_id
                AND a1.accused_master_id < a2.accused_master_id
            WHERE a1.person_id IS NOT NULL AND a2.person_id IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        log.info("Found {} co-offender pairs", rows.size());

        int batchSize = 500;
        for (int i = 0; i < rows.size(); i += batchSize) {
            List<Map<String, Object>> batch = rows.subList(i, Math.min(i + batchSize, rows.size()));
            session.executeWrite(tx -> {
                for (Map<String, Object> row : batch) {
                    String p1 = Objects.toString(row.get("person1"), null);
                    String p2 = Objects.toString(row.get("person2"), null);
                    if (p1 == null || p2 == null) continue;

                    String cypher = """
                        MATCH (a:Person {personId: $p1})
                        MATCH (b:Person {personId: $p2})
                        MERGE (a)-[r:CO_OFFENDER]-(b)
                        SET r.coOffenseCount = COALESCE(r.coOffenseCount, 0) + 1
                        """;
                    tx.run(cypher, Map.of("p1", p1, "p2", p2));
                }
                return null;
            });
        }
        log.info("Merged CO_OFFENDER relationships");
    }

    private void mergeArrestedInRelationships(Session session) {
        log.info("Merging ARRESTED_IN relationships...");
        String sql = """
            SELECT a.person_id, c.crime_no, ars.arrest_surrender_date
            FROM inv_arrest_surrender_accused iars
            JOIN arrest_surrender ars ON iars.arrest_surrender_id = ars.arrest_surrender_id
            JOIN accused a ON iars.accused_master_id = a.accused_master_id
            JOIN case_master c ON ars.case_master_id = c.case_master_id
            WHERE a.person_id IS NOT NULL AND c.crime_no IS NOT NULL
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        log.info("Found {} arrest records", rows.size());

        int batchSize = 500;
        for (int i = 0; i < rows.size(); i += batchSize) {
            List<Map<String, Object>> batch = rows.subList(i, Math.min(i + batchSize, rows.size()));
            session.executeWrite(tx -> {
                for (Map<String, Object> row : batch) {
                    String personId = Objects.toString(row.get("person_id"), null);
                    String crimeNo = Objects.toString(row.get("crime_no"), null);
                    if (personId == null || crimeNo == null) continue;

                    String cypher = """
                        MATCH (p:Person {personId: $personId})
                        MATCH (c:Case {crimeNo: $crimeNo})
                        MERGE (p)-[r:ARRESTED_IN]->(c)
                        SET r.arrestDate = $arrestDate
                        """;
                    tx.run(cypher, Map.of(
                            "personId", personId,
                            "crimeNo", crimeNo,
                            "arrestDate", toNeo4jString(row.get("arrest_surrender_date"))));
                }
                return null;
            });
        }
        log.info("Merged {} ARRESTED_IN relationships", rows.size());
    }

    private Object toNeo4jDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        return value;
    }

    private Object toNeo4jString(Object value) {
        if (value == null) return null;
        return value.toString();
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        try (Session session = neo4jDriver.session()) {
            session.executeRead(tx -> {
                var result = tx.run("MATCH (n) RETURN labels(n) AS label, count(n) AS count");
                List<Map<String, Object>> nodeCounts = new ArrayList<>();
                while (result.hasNext()) {
                    var r = result.next();
                    nodeCounts.add(Map.of(
                            "label", r.get("label").asList(Value::asString),
                            "count", r.get("count").asInt()));
                }
                stats.put("nodes", nodeCounts);

                var relResult = tx.run("MATCH ()-[r]->() RETURN type(r) AS type, count(r) AS count");
                List<Map<String, Object>> relCounts = new ArrayList<>();
                while (relResult.hasNext()) {
                    var r = relResult.next();
                    relCounts.add(Map.of(
                            "type", r.get("type").asString(),
                            "count", r.get("count").asInt()));
                }
                stats.put("relationships", relCounts);
                return null;
            });
        }
        return stats;
    }
}
