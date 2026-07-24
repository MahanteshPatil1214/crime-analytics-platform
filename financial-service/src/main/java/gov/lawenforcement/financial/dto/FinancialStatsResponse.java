package gov.lawenforcement.financial.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FinancialStatsResponse {
    private long totalTransactions;
    private long flaggedCount;
    private BigDecimal totalAmount;
}
