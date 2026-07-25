package gov.lawenforcement.notification.service;

import gov.lawenforcement.notification.dto.NotificationEvent;
import gov.lawenforcement.notification.dto.NotificationRequest;
import gov.lawenforcement.notification.dto.NotificationResponse;
import gov.lawenforcement.notification.entity.Notification;
import gov.lawenforcement.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationResponse create(NotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .read(false)
                .build();

        Notification saved = repository.save(notification);
        log.info("Notification created: id={}, user={}, type={}", saved.getId(), saved.getUserId(), saved.getType());

        sendWebSocket(saved);
        return toResponse(saved);
    }

    public void handleEvent(NotificationEvent event) {
        String userId = resolveUserId(event);
        if (userId == null) return;

        String title = buildTitle(event);
        String message = event.getMessage() != null ? event.getMessage()
                : event.getType() + " performed on " + event.getEntityType() + " by " + event.getTriggeredBy();

        NotificationRequest request = NotificationRequest.builder()
                .userId(userId)
                .type(event.getType())
                .title(title)
                .message(message)
                .entityType(event.getEntityType())
                .entityId(event.getEntityId())
                .build();

        create(request);
    }

    public List<NotificationResponse> getByUserId(String userId, int limit) {
        return repository.findByUserId(userId, limit).stream()
                .map(this::toResponse)
                .toList();
    }

    public long countUnread(String userId) {
        return repository.countUnread(userId);
    }

    public boolean markAsRead(String notificationId) {
        return repository.markAsRead(notificationId);
    }

    public Map<String, Object> getStats(String userId) {
        return Map.of(
                "total", repository.countByUserId(userId),
                "unread", repository.countUnread(userId)
        );
    }

    private void sendWebSocket(Notification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    notification.getUserId(),
                    "/queue/notifications",
                    toResponse(notification)
            );
        } catch (Exception e) {
            log.warn("Failed to send WebSocket notification: {}", e.getMessage());
        }
    }

    private String resolveUserId(NotificationEvent event) {
        if (event.getUserId() != null && !event.getUserId().isBlank()) {
            return event.getUserId();
        }
        if (event.getTriggeredBy() != null && !event.getTriggeredBy().isBlank()) {
            return event.getTriggeredBy();
        }
        return null;
    }

    private String buildTitle(NotificationEvent event) {
        return switch (event.getType() != null ? event.getType() : "") {
            case "CREATE" -> "New " + event.getEntityType() + " Created";
            case "UPDATE" -> event.getEntityType() + " Updated";
            case "UPDATE_STATUS" -> event.getEntityType() + " Status Changed";
            case "DELETE" -> event.getEntityType() + " Deleted";
            case "FLAG" -> event.getEntityType() + " Flagged for Review";
            case "UPLOAD" -> "New Evidence Uploaded";
            case "REINDEX" -> "Search Index Updated";
            default -> event.getType() + " on " + event.getEntityType();
        };
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .entityType(n.getEntityType())
                .entityId(n.getEntityId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
