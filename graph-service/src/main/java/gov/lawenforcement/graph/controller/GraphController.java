package gov.lawenforcement.graph.controller;

import gov.lawenforcement.graph.service.GraphPopulatorService;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphPopulatorService populatorService;
    private final Driver neo4jDriver;

    @PostMapping("/populate")
    public ResponseEntity<Map<String, Object>> populate() {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            populatorService.populateAll();
            result.put("status", "success");
            result.put("message", "Graph populated successfully");
            result.put("stats", populatorService.getStats());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(populatorService.getStats());
    }

    @GetMapping("/full")
    public ResponseEntity<Map<String, Object>> fullGraph() {
        String nodeCypher = """
            MATCH (n)
            RETURN n.personId AS personId, n.crimeNo AS crimeNo,
                   n.name AS name, n.personType AS personType,
                   labels(n) AS labels
            LIMIT 1000
            """;

        String relCypher = """
            MATCH (a)-[r]->(b)
            RETURN type(r) AS relType,
                   COALESCE(a.personId, a.crimeNo) AS fromId,
                   COALESCE(b.personId, b.crimeNo) AS toId
            LIMIT 2000
            """;

        try (Session session = neo4jDriver.session()) {
            return session.executeRead(tx -> {
                Map<String, Object> response = new LinkedHashMap<>();

                var nodeResult = tx.run(nodeCypher);
                List<Map<String, Object>> nodes = new ArrayList<>();
                while (nodeResult.hasNext()) {
                    var r = nodeResult.next();
                    Map<String, Object> n = new LinkedHashMap<>();
                    n.put("personId", r.get("personId").isNull() ? null : r.get("personId").asString());
                    n.put("crimeNo", r.get("crimeNo").isNull() ? null : r.get("crimeNo").asString());
                    n.put("name", r.get("name").isNull() ? null : r.get("name").asString());
                    n.put("personType", r.get("personType").isNull() ? null : r.get("personType").asString());
                    n.put("labels", r.get("labels").asList(Value::asString));
                    nodes.add(n);
                }
                response.put("nodes", nodes);

                var relResult = tx.run(relCypher);
                List<Map<String, Object>> rels = new ArrayList<>();
                while (relResult.hasNext()) {
                    var r = relResult.next();
                    Map<String, Object> rel = new LinkedHashMap<>();
                    rel.put("type", r.get("relType").asString());
                    rel.put("fromId", r.get("fromId").asString());
                    rel.put("toId", r.get("toId").asString());
                    rels.add(rel);
                }
                response.put("relationships", rels);

                return ResponseEntity.ok(response);
            });
        }
    }

    @GetMapping("/person/{personId}/network")
    public ResponseEntity<Map<String, Object>> personNetwork(
            @PathVariable String personId,
            @RequestParam(defaultValue = "2") int hops) {
        String nodeCypher = "MATCH (p:Person {personId: $personId})-[*1.."
                + hops + "]-(connected) "
                + "RETURN DISTINCT connected.personId AS personId, "
                + "connected.crimeNo AS crimeNo, "
                + "connected.name AS name, "
                + "connected.personType AS personType, "
                + "labels(connected) AS labels "
                + "LIMIT 500";

        String relCypher = """
            MATCH (p:Person {personId: $personId})-[rel]->(other)
            WHERE other IS NOT NULL
            RETURN DISTINCT
                type(rel) AS relType,
                p.personId AS fromId,
                COALESCE(other.personId, other.crimeNo) AS toId
            LIMIT 500
            """;

        String relCypher2 = """
            MATCH (other)-[rel]->(p:Person {personId: $personId})
            WHERE other IS NOT NULL
            RETURN DISTINCT
                type(rel) AS relType,
                COALESCE(other.personId, other.crimeNo) AS fromId,
                p.personId AS toId
            LIMIT 500
            """;

        try (Session session = neo4jDriver.session()) {
            return session.executeRead(tx -> {
                Map<String, Object> response = new LinkedHashMap<>();

                var nodeResult = tx.run(nodeCypher, Map.of("personId", personId));
                List<Map<String, Object>> nodes = new ArrayList<>();
                while (nodeResult.hasNext()) {
                    var r = nodeResult.next();
                    Map<String, Object> node = new LinkedHashMap<>();
                    node.put("personId", r.get("personId").isNull() ? null : r.get("personId").asString());
                    node.put("crimeNo", r.get("crimeNo").isNull() ? null : r.get("crimeNo").asString());
                    node.put("name", r.get("name").isNull() ? null : r.get("name").asString());
                    node.put("personType", r.get("personType").isNull() ? null : r.get("personType").asString());
                    node.put("labels", r.get("labels").asList(Value::asString));
                    nodes.add(node);
                }
                response.put("nodes", nodes);

                List<Map<String, Object>> rels = new ArrayList<>();
                Set<String> seenRels = new HashSet<>();

                var relResult1 = tx.run(relCypher, Map.of("personId", personId));
                while (relResult1.hasNext()) {
                    var r = relResult1.next();
                    String key = r.get("relType").asString() + "|" + r.get("fromId").asString() + "|" + r.get("toId").asString();
                    if (seenRels.add(key)) {
                        Map<String, Object> rel = new LinkedHashMap<>();
                        rel.put("type", r.get("relType").asString());
                        rel.put("fromId", r.get("fromId").asString());
                        rel.put("toId", r.get("toId").asString());
                        rels.add(rel);
                    }
                }

                var relResult2 = tx.run(relCypher2, Map.of("personId", personId));
                while (relResult2.hasNext()) {
                    var r = relResult2.next();
                    String key = r.get("relType").asString() + "|" + r.get("fromId").asString() + "|" + r.get("toId").asString();
                    if (seenRels.add(key)) {
                        Map<String, Object> rel = new LinkedHashMap<>();
                        rel.put("type", r.get("relType").asString());
                        rel.put("fromId", r.get("fromId").asString());
                        rel.put("toId", r.get("toId").asString());
                        rels.add(rel);
                    }
                }

                response.put("relationships", rels);
                response.put("personId", personId);
                response.put("hops", hops);
                return ResponseEntity.ok(response);
            });
        }
    }

    @GetMapping("/case/{crimeNo}/network")
    public ResponseEntity<Map<String, Object>> caseNetwork(@PathVariable String crimeNo) {
        String cypher = """
            MATCH (c:Case {crimeNo: $crimeNo})<-[r]-(p:Person)
            RETURN collect(DISTINCT {
                personId: p.personId,
                name: p.name,
                personType: p.personType,
                age: p.age,
                gender: p.gender
            }) AS persons,
            collect(DISTINCT {
                type: type(r),
                personId: p.personId
            }) AS relationships
            """;

        try (Session session = neo4jDriver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher, Map.of("crimeNo", crimeNo));
                if (!result.hasNext()) {
                    return ResponseEntity.ok(Map.of("crimeNo", crimeNo, "persons", List.of(), "relationships", List.of()));
                }
                var record = result.next();
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("crimeNo", crimeNo);
                response.put("persons", record.get("persons").asList(v -> v.asMap()));
                response.put("relationships", record.get("relationships").asList(v -> v.asMap()));
                return ResponseEntity.ok(response);
            });
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        String cypher = """
            MATCH (p:Person)
            WHERE toLower(p.name) CONTAINS toLower($query)
            RETURN p.personId AS personId,
                   p.name AS name,
                   p.age AS age,
                   p.gender AS gender,
                   p.personType AS personType
            LIMIT 50
            """;

        try (Session session = neo4jDriver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher, Map.of("query", q));
                List<Map<String, Object>> persons = new ArrayList<>();
                while (result.hasNext()) {
                    var r = result.next();
                    Map<String, Object> person = new LinkedHashMap<>();
                    person.put("personId", r.get("personId").asString());
                    person.put("name", r.get("name").asString());
                    person.put("age", r.get("age").isNull() ? null : r.get("age").asInt());
                    person.put("gender", r.get("gender").isNull() ? null : r.get("gender").asString());
                    person.put("personType", r.get("personType").asString());
                    persons.add(person);
                }
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("query", q);
                response.put("results", persons);
                response.put("count", persons.size());
                return ResponseEntity.ok(response);
            });
        }
    }

    @GetMapping("/communities")
    public ResponseEntity<Map<String, Object>> communities() {
        String cypher = """
            MATCH (p:Person)-[r:CO_OFFENDER]-(other:Person)
            WITH p, collect(DISTINCT other) AS neighbors, count(DISTINCT other) AS degree
            ORDER BY degree DESC
            WITH collect({personId: p.personId, name: p.name, degree: degree}) AS allNodes,
                 collect(p) AS nodeList
            UNWIND range(0, size(nodeList) - 1) AS idx
            WITH allNodes[idx] AS nodeInfo, nodeList[idx] AS node
            OPTIONAL MATCH (node)-[:CO_OFFENDER*1..3]-(connected:Person)
            WITH nodeInfo, node, collect(DISTINCT connected.personId) AS reachable
            RETURN nodeInfo.personId AS personId,
                   nodeInfo.name AS name,
                   nodeInfo.degree AS degree,
                   reachable AS communityMembers,
                   size(reachable) AS communitySize
            ORDER BY communitySize DESC
            LIMIT 50
            """;

        try (Session session = neo4jDriver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher);
                List<Map<String, Object>> communities = new ArrayList<>();
                Set<String> seen = new HashSet<>();
                while (result.hasNext()) {
                    var r = result.next();
                    String personId = r.get("personId").asString();
                    if (seen.add(personId)) {
                        Map<String, Object> community = new LinkedHashMap<>();
                        community.put("personId", personId);
                        community.put("name", r.get("name").asString());
                        community.put("degree", r.get("degree").asInt());
                        community.put("communityMembers", r.get("communityMembers").asList(Value::asString));
                        community.put("communitySize", r.get("communitySize").asInt());
                        communities.add(community);
                    }
                }
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("communities", communities);
                response.put("count", communities.size());
                return ResponseEntity.ok(response);
            });
        }
    }

    @GetMapping("/path")
    public ResponseEntity<Map<String, Object>> shortestPath(
            @RequestParam String from,
            @RequestParam String to) {
        String cypher = """
            MATCH (source:Person {personId: $from})
            MATCH (target:Person {personId: $to})
            MATCH path = shortestPath(
                (source)-[:CO_OFFENDER|ACCUSED_OF|VICTIM_OF|COMPLAINT_OF|ARRESTED_IN*]-(target)
            )
            RETURN [n IN nodes(path) | {
                personId: n.personId,
                crimeNo: n.crimeNo,
                name: n.name,
                personType: n.personType,
                labels: labels(n)
            }] AS nodes,
            [r IN relationships(path) | {
                type: type(r),
                caseCrimeNo: r.caseCrimeNo
            }] AS relationships,
            length(path) AS pathLength
            """;

        try (Session session = neo4jDriver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher, Map.of("from", from, "to", to));
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("from", from);
                response.put("to", to);

                if (result.hasNext()) {
                    var record = result.next();
                    response.put("found", true);
                    response.put("pathLength", record.get("pathLength").asInt());
                    response.put("nodes", record.get("nodes").asList(v -> v.asMap()));
                    response.put("relationships", record.get("relationships").asList(v -> v.asMap()));
                } else {
                    response.put("found", false);
                    response.put("pathLength", -1);
                    response.put("nodes", List.of());
                    response.put("relationships", List.of());
                }
                return ResponseEntity.ok(response);
            });
        }
    }
}
