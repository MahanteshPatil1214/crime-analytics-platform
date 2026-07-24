package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ShortestPathResponse {
    private String from;
    private String to;
    private boolean found;
    private int pathLength;
    private List<Map<String, Object>> nodes;
    private List<Map<String, Object>> relationships;
}
