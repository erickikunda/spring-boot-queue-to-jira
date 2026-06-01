package com.example.jiraticketservice.consumer;

import com.example.jiraticketservice.event.SupportEscalationRequestedEvent;
import com.example.jiraticketservice.exception.*;
import com.example.jiraticketservice.observability.MetricsService;
import com.example.jiraticketservice.service.JiraTicketOrchestrator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EscalationEventConsumer {
    private final ObjectMapper objectMapper; private final JiraTicketOrchestrator orchestrator; private final MetricsService metrics;
    public EscalationEventConsumer(ObjectMapper objectMapper, JiraTicketOrchestrator orchestrator, MetricsService metrics) {
        this.objectMapper = objectMapper; this.orchestrator = orchestrator; this.metrics = metrics;
    }
    @RabbitListener(queues = "${app.queue.escalation-queue}")
    public void receive(String rawMessage) {
        var sample = metrics.startProcessing(); metrics.incrementConsumed();
        try {
            orchestrator.process(objectMapper.readValue(rawMessage, SupportEscalationRequestedEvent.class));
        } catch (JsonProcessingException | InvalidEscalationEventException e) {
            metrics.incrementInvalid();
            throw new AmqpRejectAndDontRequeueException("Invalid escalation event", e);
        } catch (NonRetryableJiraException e) {
            throw new AmqpRejectAndDontRequeueException("Non-retryable Jira failure", e);
        } finally {
            metrics.stopProcessing(sample);
        }
    }
}
