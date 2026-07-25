package gov.lawenforcement.notification.kafka;

import gov.lawenforcement.notification.dto.NotificationEvent;
import gov.lawenforcement.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "audit-events", groupId = "notification-service")
    public void onAuditEvent(NotificationEvent event) {
        log.info("Received audit event: type={}, entityType={}, entityId={}",
                event.getType(), event.getEntityType(), event.getEntityId());
        try {
            notificationService.handleEvent(event);
        } catch (Exception e) {
            log.error("Failed to process audit event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "case-events", groupId = "notification-service")
    public void onCaseEvent(NotificationEvent event) {
        log.info("Received case event: type={}", event.getType());
        try {
            notificationService.handleEvent(event);
        } catch (Exception e) {
            log.error("Failed to process case event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "financial-alerts", groupId = "notification-service")
    public void onFinancialAlert(NotificationEvent event) {
        log.info("Received financial alert: type={}, entityId={}", event.getType(), event.getEntityId());
        try {
            notificationService.handleEvent(event);
        } catch (Exception e) {
            log.error("Failed to process financial alert: {}", e.getMessage());
        }
    }
}
