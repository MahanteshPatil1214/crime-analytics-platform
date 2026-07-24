package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommunityDto {
    private String personId;
    private String name;
    private int degree;
    private List<String> communityMembers;
    private int communitySize;
}
