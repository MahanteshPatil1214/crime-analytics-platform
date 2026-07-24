package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GraphNode {
    private String personId;
    private String crimeNo;
    private String name;
    private String personType;
    private List<String> labels;
}
