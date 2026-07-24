package gov.lawenforcement.conversational.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatMessageRequest {
    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;

    @Size(max = 100, message = "Session ID must not exceed 100 characters")
    private String sessionId = "default";
}
