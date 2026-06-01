package com.example.jiraticketservice.event;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SupportEscalationRequestedEvent(
        String eventId, String eventType, Instant occurredAt, UUID escalationId, UUID conversationId,
        String userId, String customerEmail, String customerName, String question, String generatedAnswer,
        Double confidenceScore, String confidenceReason, List<RetrievedSource> retrievedSources,
        Map<String, Object> metadata) {}
