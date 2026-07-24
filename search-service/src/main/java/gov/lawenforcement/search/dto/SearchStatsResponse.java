package gov.lawenforcement.search.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchStatsResponse {
    private long casesCount;
    private long personsCount;
    private long financialCount;
}
