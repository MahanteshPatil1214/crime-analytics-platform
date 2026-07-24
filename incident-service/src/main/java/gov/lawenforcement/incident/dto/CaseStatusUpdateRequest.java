package gov.lawenforcement.incident.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CaseStatusUpdateRequest {
    @NotNull(message = "Status ID is required")
    private Integer statusId;
}
