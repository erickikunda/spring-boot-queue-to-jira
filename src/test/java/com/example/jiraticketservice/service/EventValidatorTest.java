package com.example.jiraticketservice.service;

import com.example.jiraticketservice.event.SupportEscalationRequestedEvent;
import com.example.jiraticketservice.exception.InvalidEscalationEventException;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EventValidatorTest {
    private final EventValidator validator = new EventValidator();
    @Test void acceptsValidEvent() { assertDoesNotThrow(() -> validator.validate(event("question", .42))); }
    @Test void rejectsBlankQuestion() { assertThrows(InvalidEscalationEventException.class, () -> validator.validate(event(" ", .42))); }
    @Test void rejectsOutOfRangeConfidence() { assertThrows(InvalidEscalationEventException.class, () -> validator.validate(event("question", 1.1))); }
    private SupportEscalationRequestedEvent event(String question, double score) {
        return new SupportEscalationRequestedEvent("evt-1", "support_escalation_requested", Instant.now(), UUID.randomUUID(),
                UUID.randomUUID(), null, null, null, question, null, score, null, null, null);
    }
}
