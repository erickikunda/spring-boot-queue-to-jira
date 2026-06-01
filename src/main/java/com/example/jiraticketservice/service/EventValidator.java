package com.example.jiraticketservice.service;

import com.example.jiraticketservice.event.SupportEscalationRequestedEvent;
import com.example.jiraticketservice.exception.InvalidEscalationEventException;
import org.springframework.stereotype.Service;

@Service
public class EventValidator {
    public void validate(SupportEscalationRequestedEvent e) {
        require(e != null, "event is required");
        require(notBlank(e.eventId()), "eventId is required");
        require("support_escalation_requested".equals(e.eventType()), "eventType must be support_escalation_requested");
        require(e.occurredAt() != null, "occurredAt is required");
        require(e.escalationId() != null, "escalationId is required");
        require(e.conversationId() != null, "conversationId is required");
        require(notBlank(e.question()), "question is required");
        require(e.confidenceScore() != null && e.confidenceScore() >= 0 && e.confidenceScore() <= 1,
                "confidenceScore must be between 0 and 1");
    }
    private boolean notBlank(String value) { return value != null && !value.isBlank(); }
    private void require(boolean condition, String message) {
        if (!condition) throw new InvalidEscalationEventException(message);
    }
}
