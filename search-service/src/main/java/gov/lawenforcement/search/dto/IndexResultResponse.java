package gov.lawenforcement.search.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndexResultResponse {
    private long casesIndexed;
    private long personsIndexed;
    private long financialIndexed;
}
