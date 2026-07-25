package gov.lawenforcement.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String type;
    private String entityType;
    private String entityId;
    private String userId;
    private String message;
    private String triggeredBy;
}
