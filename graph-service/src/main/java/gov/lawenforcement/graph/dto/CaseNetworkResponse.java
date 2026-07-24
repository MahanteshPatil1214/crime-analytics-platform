package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class CaseNetworkResponse {
    private String crimeNo;
    private List<Map<String, Object>> persons;
    private List<Map<String, Object>> relationships;
}
