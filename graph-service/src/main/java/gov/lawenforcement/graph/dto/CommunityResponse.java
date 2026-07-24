package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommunityResponse {
    private List<CommunityDto> communities;
    private int count;
}
