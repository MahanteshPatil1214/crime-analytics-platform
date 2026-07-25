package gov.lawenforcement.notification.controller;

import gov.lawenforcement.notification.dto.NotificationRequest;
import gov.lawenforcement.notification.dto.NotificationResponse;
import gov.lawenforcement.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.create(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(notificationService.getByUserId(userId, limit));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getStats(userId));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Boolean>> markAsRead(@PathVariable String notificationId) {
        boolean success = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "notification-service"));
    }
}
