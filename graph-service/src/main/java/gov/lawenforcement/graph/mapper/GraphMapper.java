package gov.lawenforcement.graph.mapper;

import gov.lawenforcement.graph.dto.GraphNode;
import gov.lawenforcement.graph.dto.GraphRelationship;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraphMapper {

    public GraphNode toNode(Record record) {
        return GraphNode.builder()
                .personId(nullableString(record, "personId"))
                .crimeNo(nullableString(record, "crimeNo"))
                .name(nullableString(record, "name"))
                .personType(nullableString(record, "personType"))
                .labels(record.get("labels").asList(Value::asString))
                .build();
    }

    public GraphRelationship toRelationship(Record record) {
        return GraphRelationship.builder()
                .type(record.get("relType").asString())
                .fromId(record.get("fromId").asString())
                .toId(record.get("toId").asString())
                .build();
    }

    private String nullableString(Record record, String field) {
        Value value = record.get(field);
        return value.isNull() ? null : value.asString();
    }
}
