package gov.lawenforcement.notification.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.lawenforcement.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String NOTIFICATION_KEY_PREFIX = "notifications:";
    private static final String USER_INDEX_PREFIX = "notification_index:";
    private static final long TTL_DAYS = 30;

    public Notification save(Notification notification) {
        try {
            if (notification.getId() == null) {
                notification.setId(UUID.randomUUID().toString());
            }
            if (notification.getCreatedAt() == null) {
                notification.setCreatedAt(Instant.now());
            }

            String json = objectMapper.writeValueAsString(notification);
            String key = NOTIFICATION_KEY_PREFIX + notification.getId();
            String indexKey = USER_INDEX_PREFIX + notification.getUserId();

            redisTemplate.opsForValue().set(key, json, TTL_DAYS, TimeUnit.DAYS);
            redisTemplate.opsForZSet().add(indexKey, notification.getId(),
                    notification.getCreatedAt().toEpochMilli());
            redisTemplate.expire(indexKey, TTL_DAYS, TimeUnit.DAYS);

            return notification;
        } catch (Exception e) {
            log.error("Failed to save notification: {}", e.getMessage());
            throw new RuntimeException("Failed to save notification", e);
        }
    }

    public List<Notification> findByUserId(String userId, int limit) {
        try {
            String indexKey = USER_INDEX_PREFIX + userId;
            var ids = redisTemplate.opsForZSet().reverseRange(indexKey, 0, limit - 1);
            if (ids == null || ids.isEmpty()) return List.of();

            List<Notification> notifications = new ArrayList<>();
            for (Object id : ids) {
                String json = redisTemplate.opsForValue().get(NOTIFICATION_KEY_PREFIX + id);
                if (json != null) {
                    notifications.add(objectMapper.readValue(json, Notification.class));
                }
            }
            notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
            return notifications;
        } catch (Exception e) {
            log.error("Failed to find notifications for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    public long countUnread(String userId) {
        try {
            List<Notification> notifications = findByUserId(userId, 100);
            return notifications.stream().filter(n -> !n.isRead()).count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean markAsRead(String notificationId) {
        try {
            String json = redisTemplate.opsForValue().get(NOTIFICATION_KEY_PREFIX + notificationId);
            if (json == null) return false;

            Notification notification = objectMapper.readValue(json, Notification.class);
            notification.setRead(true);
            redisTemplate.opsForValue().set(NOTIFICATION_KEY_PREFIX + notificationId,
                    objectMapper.writeValueAsString(notification), TTL_DAYS, TimeUnit.DAYS);
            return true;
        } catch (Exception e) {
            log.error("Failed to mark notification as read: {}", e.getMessage());
            return false;
        }
    }

    public long countByUserId(String userId) {
        try {
            String indexKey = USER_INDEX_PREFIX + userId;
            Long count = redisTemplate.opsForZSet().zCard(indexKey);
            return count != null ? count : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
