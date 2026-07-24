package gov.lawenforcement.incident.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CrimeHeadStatsDto {
    private Integer crimeHeadId;
    private Long count;
}
