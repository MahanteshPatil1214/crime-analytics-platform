package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GraphResponse {
    private List<GraphNode> nodes;
    private List<GraphRelationship> relationships;
}
