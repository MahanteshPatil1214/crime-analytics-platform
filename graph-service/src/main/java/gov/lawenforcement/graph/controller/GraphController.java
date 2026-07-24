package gov.lawenforcement.graph.controller;

import gov.lawenforcement.graph.dto.*;
import gov.lawenforcement.graph.mapper.GraphMapper;
import gov.lawenforcement.graph.service.GraphPopulatorService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphPopulatorService populatorService;
    private final Driver neo4jDriver;
    private final GraphMapper graphMapper;

    @PostMapping("/populate")
    @PreAuthorize("hasRole('ADMIN')")
    @Timed(value = "graph.populate", description = "Time to populate the graph database")
    public ResponseEntity<Map<String, Object>> populate() {
        populatorService.populateAll();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "success");
        result.put("message", "Graph populated successfully");
        result.put("stats", populatorService.getStats());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(populatorService.getStats());
    }

    @GetMapping("/full")
    @Timed(value = "graph.full", description = "Time to fetch full graph")
    public ResponseEntity<GraphResponse> fullGraph() {
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
                List<GraphNode> nodes = tx.run(nodeCypher).list(graphMapper::toNode);
                List<GraphRelationship> rels = tx.run(relCypher).list(graphMapper::toRelationship);
                return ResponseEntity.ok(GraphResponse.builder().nodes(nodes).relationships(rels).build());
            });
        }
    }

    @GetMapping("/person/{personId}/network")
    public ResponseEntity<PersonNetworkResponse> personNetwork(
            @PathVariable String personId,
            @RequestParam(defaultValue = "2") int hops) {
        if (personId == null || personId.isBlank() || !personId.matches("^[A-Za-z0-9_-]{1,50}$")) {
            throw new IllegalArgumentException("Invalid person ID format");
        }
        int validatedHops = Math.min(Math.max(hops, 1), 6);
        String nodeCypher = "MATCH (p:Person {personId: $personId})-[*1.."
                + validatedHops + "]-(connected) "
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
                List<GraphNode> nodes = tx.run(nodeCypher, Map.of("personId", personId))
                        .list(graphMapper::toNode);

                Set<String> seenRels = new HashSet<>();
                List<GraphRelationship> rels = new ArrayList<>();

                tx.run(relCypher, Map.of("personId", personId)).forEachRemaining(r -> {
                    String key = r.get("relType").asString() + "|" + r.get("fromId").asString() + "|" + r.get("toId").asString();
                    if (seenRels.add(key)) {
                        rels.add(graphMapper.toRelationship(r));
                    }
                });

                tx.run(relCypher2, Map.of("personId", personId)).forEachRemaining(r -> {
                    String key = r.get("relType").asString() + "|" + r.get("fromId").asString() + "|" + r.get("toId").asString();
                    if (seenRels.add(key)) {
                        rels.add(graphMapper.toRelationship(r));
                    }
                });

                return ResponseEntity.ok(PersonNetworkResponse.builder()
                        .personId(personId)
                        .hops(validatedHops)
                        .nodes(nodes)
                        .relationships(rels)
                        .build());
            });
        }
    }

    @GetMapping("/case/{crimeNo}/network")
    public ResponseEntity<CaseNetworkResponse> caseNetwork(@PathVariable String crimeNo) {
        if (crimeNo == null || crimeNo.isBlank() || !crimeNo.matches("^[A-Za-z0-9/]{1,50}$")) {
            throw new IllegalArgumentException("Invalid crime number format");
        }
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
                    return ResponseEntity.ok(CaseNetworkResponse.builder()
                            .crimeNo(crimeNo).persons(List.of()).relationships(List.of()).build());
                }
                var record = result.next();
                return ResponseEntity.ok(CaseNetworkResponse.builder()
                        .crimeNo(crimeNo)
                        .persons(record.get("persons").asList(v -> v.asMap()))
                        .relationships(record.get("relationships").asList(v -> v.asMap()))
                        .build());
            });
        }
    }

    @GetMapping("/search")
    public ResponseEntity<PersonSearchResponse> search(@RequestParam String q) {
        if (q == null || q.isBlank() || q.length() > 100) {
            throw new IllegalArgumentException("Search query must be 1-100 characters");
        }
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
                List<PersonSearchResponse.PersonSearchResult> persons = new ArrayList<>();
                while (result.hasNext()) {
                    var r = result.next();
                    persons.add(PersonSearchResponse.PersonSearchResult.builder()
                            .personId(r.get("personId").asString())
                            .name(r.get("name").asString())
                            .age(r.get("age").isNull() ? null : r.get("age").asInt())
                            .gender(r.get("gender").isNull() ? null : r.get("gender").asString())
                            .personType(r.get("personType").asString())
                            .build());
                }
                return ResponseEntity.ok(PersonSearchResponse.builder()
                        .query(q).results(persons).count(persons.size()).build());
            });
        }
    }

    @GetMapping("/communities")
    @Timed(value = "graph.communities", description = "Time to detect communities")
    public ResponseEntity<CommunityResponse> communities() {
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
                List<CommunityDto> communities = new ArrayList<>();
                Set<String> seen = new HashSet<>();
                while (result.hasNext()) {
                    var r = result.next();
                    String personId = r.get("personId").asString();
                    if (seen.add(personId)) {
                        communities.add(CommunityDto.builder()
                                .personId(personId)
                                .name(r.get("name").asString())
                                .degree(r.get("degree").asInt())
                                .communityMembers(r.get("communityMembers").asList(Value::asString))
                                .communitySize(r.get("communitySize").asInt())
                                .build());
                    }
                }
                return ResponseEntity.ok(CommunityResponse.builder()
                        .communities(communities).count(communities.size()).build());
            });
        }
    }

    @GetMapping("/path")
    @Timed(value = "graph.shortestPath", description = "Time to find shortest path")
    public ResponseEntity<ShortestPathResponse> shortestPath(
            @RequestParam String from,
            @RequestParam String to) {
        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new IllegalArgumentException("'from' and 'to' person IDs are required");
        }
        if (from.length() > 50 || to.length() > 50) {
            throw new IllegalArgumentException("Person ID must not exceed 50 characters");
        }
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
                ShortestPathResponse.ShortestPathResponseBuilder builder = ShortestPathResponse.builder()
                        .from(from).to(to);

                if (result.hasNext()) {
                    var record = result.next();
                    builder.found(true)
                            .pathLength(record.get("pathLength").asInt())
                            .nodes(record.get("nodes").asList(v -> v.asMap()))
                            .relationships(record.get("relationships").asList(v -> v.asMap()));
                } else {
                    builder.found(false).pathLength(-1).nodes(List.of()).relationships(List.of());
                }
                return ResponseEntity.ok(builder.build());
            });
        }
    }
}
