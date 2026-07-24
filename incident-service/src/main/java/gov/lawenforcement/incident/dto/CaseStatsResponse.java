package gov.lawenforcement.incident.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaseStatsResponse {
    private long totalCases;
    private long openCases;
    private long underInvestigation;
    private long chargeSheeted;
    private long closed;
}
