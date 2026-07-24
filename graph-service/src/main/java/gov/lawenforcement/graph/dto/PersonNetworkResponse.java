package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PersonNetworkResponse {
    private String personId;
    private int hops;
    private List<GraphNode> nodes;
    private List<GraphRelationship> relationships;
}
