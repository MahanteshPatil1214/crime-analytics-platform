package gov.lawenforcement.messaging;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DomainEvent<T>(
    String eventId,
    String eventType,
    String aggregateType,
    String aggregateId,
    T payload,
    Map<String, String> metadata,
    Instant occurredAt,
    String correlationId
) {
    public static <T> DomainEvent<T> of(String eventType, String aggregateType, String aggregateId, T payload) {
        return new DomainEvent<>(
            UUID.randomUUID().toString(), eventType, aggregateType, aggregateId,
            payload,
            Map.of("version", "1", "source", "crime-analytics-platform"),
            Instant.now(),
            UUID.randomUUID().toString()
        );
    }
}
