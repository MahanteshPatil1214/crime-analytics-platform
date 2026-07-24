package gov.lawenforcement.financial.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FlagTransactionRequest {
    @NotBlank(message = "Flag reason is required")
    private String reason;
}
