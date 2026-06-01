package com.example.jiraticketservice.service;

import com.example.jiraticketservice.config.ApplicationProperties;
import com.example.jiraticketservice.entity.MessageEntity;
import com.example.jiraticketservice.event.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class JiraIssueBuilderTest {
    private final JiraIssueBuilder builder = new JiraIssueBuilder(new ApplicationProperties(
            new ApplicationProperties.Jira("https://jira.example", "a", "b", "SUP", "Support Request"), null));
    @Test void truncatesSummaryAndMapsHighPriority() {
        var request = builder.build(event("x".repeat(100), .2, Map.of("product", "Billing App")), new ConversationContextService.ConversationContext(List.of()));
        assertEquals("Chatbot escalation: " + "x".repeat(80), request.fields().summary());
        assertEquals("High", request.fields().priority().name());
        assertTrue(request.fields().labels().contains("billing-app"));
    }
    @Test void formatsDescriptionWithContext() {
        var conversationId = UUID.randomUUID();
        var context = new ConversationContextService.ConversationContext(List.of(new MessageEntity(UUID.randomUUID(), conversationId, "user", "Hello", LocalDateTime.now())));
        String description = builder.build(event("Need help", .5, Map.of()), context).fields().description();
        assertTrue(description.contains("No generated answer was returned"));
        assertTrue(description.contains("User: Hello"));
    }
    private SupportEscalationRequestedEvent event(String question, double score, Map<String, Object> metadata) {
        return new SupportEscalationRequestedEvent("evt", "support_escalation_requested", Instant.now(), UUID.randomUUID(),
                UUID.randomUUID(), null, "customer@example.com", "Jane", question, null, score, "weak match",
                List.of(new RetrievedSource("Billing", "https://example.com")), metadata);
    }
}
