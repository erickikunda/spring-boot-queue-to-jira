package com.example.jiraticketservice.service;

import com.example.jiraticketservice.config.ApplicationProperties;
import com.example.jiraticketservice.event.SupportEscalationRequestedEvent;
import com.example.jiraticketservice.exception.*;
import com.example.jiraticketservice.jira.JiraClient;
import com.example.jiraticketservice.observability.MetricsService;
import org.slf4j.*;
import org.springframework.stereotype.Service;

@Service
public class JiraTicketOrchestrator {
    private static final Logger log = LoggerFactory.getLogger(JiraTicketOrchestrator.class);
    private final EventValidator validator;
    private final IdempotencyService idempotency;
    private final EscalationService escalations;
    private final ConversationContextService conversations;
    private final JiraIssueBuilder builder;
    private final JiraClient jira;
    private final JiraTicketAttemptService attempts;
    private final MetricsService metrics;
    private final ApplicationProperties properties;

    public JiraTicketOrchestrator(EventValidator validator, IdempotencyService idempotency, EscalationService escalations,
                                  ConversationContextService conversations, JiraIssueBuilder builder, JiraClient jira,
                                  JiraTicketAttemptService attempts, MetricsService metrics, ApplicationProperties properties) {
        this.validator = validator;
        this.idempotency = idempotency;
        this.escalations = escalations;
        this.conversations = conversations;
        this.builder = builder;
        this.jira = jira;
        this.attempts = attempts;
        this.metrics = metrics;
        this.properties = properties;
    }

    public void process(SupportEscalationRequestedEvent event) {
        validator.validate(event);
        if (idempotency.hasProcessed(event.eventId())) {
            duplicate(event, "processed_event_exists");
            return;
        }
        if (escalations.claimForProcessing(event.escalationId()).isEmpty()) {
            duplicate(event, "claim_skipped");
            return;
        }
        var request = builder.build(event, conversations.loadContext(event.conversationId()));
        try {
            var response = metrics.timeJira(() -> jira.createIssue(request));
            String url = properties.jira().baseUrl() + "/browse/" + response.key();
            escalations.markTicketCreated(event.escalationId(), response.key(), url);
            attempts.recordSuccess(event, request, response);
            idempotency.markProcessed(event.eventId(), event.escalationId());
            metrics.incrementCreated();
            log.info("eventId={} escalationId={} conversationId={} jiraTicketKey={} status=TICKET_CREATED",
                    event.eventId(), event.escalationId(), event.conversationId(), response.key());
        } catch (RetryableJiraException e) {
            attempts.recordRetryableFailure(event, request, e);
            escalations.markFailed(event.escalationId(), e.getMessage());
            metrics.incrementFailed();
            throw e;
        } catch (NonRetryableJiraException e) {
            attempts.recordNonRetryableFailure(event, request, e);
            escalations.markFailed(event.escalationId(), e.getMessage());
            metrics.incrementFailed();
            throw e;
        }
    }

    private void duplicate(SupportEscalationRequestedEvent e, String status) {
        metrics.incrementDuplicateIgnored();
        log.info("eventId={} escalationId={} conversationId={} status={}", e.eventId(), e.escalationId(), e.conversationId(), status);
    }
}
