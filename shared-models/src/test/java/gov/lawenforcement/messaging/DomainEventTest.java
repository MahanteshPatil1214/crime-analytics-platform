package gov.lawenforcement.messaging;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DomainEventTest {

    @Test
    void of_createsEventWithAllFields() {
        DomainEvent<String> event = DomainEvent.of(
                "CASE_CREATED", "CaseMaster", "123", "test payload");

        assertNotNull(event.eventId());
        assertEquals("CASE_CREATED", event.eventType());
        assertEquals("CaseMaster", event.aggregateType());
        assertEquals("123", event.aggregateId());
        assertEquals("test payload", event.payload());
        assertNotNull(event.occurredAt());
        assertNotNull(event.correlationId());
        assertNotNull(event.metadata());
    }

    @Test
    void of_setsDefaultMetadata() {
        DomainEvent<String> event = DomainEvent.of(
                "CASE_UPDATED", "CaseMaster", "456", "payload");

        Map<String, String> metadata = event.metadata();
        assertEquals("1", metadata.get("version"));
        assertEquals("crime-analytics-platform", metadata.get("source"));
    }

    @Test
    void of_uniqueEventIds() {
        DomainEvent<String> event1 = DomainEvent.of("EVENT", "Type", "1", "a");
        DomainEvent<String> event2 = DomainEvent.of("EVENT", "Type", "1", "a");
        assertNotEquals(event1.eventId(), event2.eventId());
    }

    @Test
    void of_uniqueCorrelationIds() {
        DomainEvent<String> event1 = DomainEvent.of("EVENT", "Type", "1", "a");
        DomainEvent<String> event2 = DomainEvent.of("EVENT", "Type", "1", "a");
        assertNotEquals(event1.correlationId(), event2.correlationId());
    }

    @Test
    void of_nullPayload() {
        DomainEvent<Object> event = DomainEvent.of("EVENT", "Type", "1", null);
        assertNull(event.payload());
    }

    @Test
    void of_recordComponents_areCorrect() {
        DomainEvent<String> event = DomainEvent.of("TEST", "TestAggregate", "999", "data");
        assertTrue(event instanceof Record);
    }
}
