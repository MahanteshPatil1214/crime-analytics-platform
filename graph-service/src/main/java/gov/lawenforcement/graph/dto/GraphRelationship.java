package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraphRelationship {
    private String type;
    private String fromId;
    private String toId;
}
