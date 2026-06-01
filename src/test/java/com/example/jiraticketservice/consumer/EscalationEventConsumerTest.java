package com.example.jiraticketservice.consumer;

import com.example.jiraticketservice.exception.InvalidEscalationEventException;
import com.example.jiraticketservice.observability.MetricsService;
import com.example.jiraticketservice.service.JiraTicketOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EscalationEventConsumerTest {
    private final TestOrchestrator orchestrator = new TestOrchestrator();
    private final MetricsService metrics = new MetricsService(new SimpleMeterRegistry());
    private final EscalationEventConsumer consumer = new EscalationEventConsumer(new ObjectMapper(), orchestrator, metrics);

    @Test void malformedJsonIsRejectedWithoutRequeue() {
        assertThrows(AmqpRejectAndDontRequeueException.class, () -> consumer.receive("{"));
    }

    @Test void missingRequiredFieldsAreRejectedWithoutRequeue() {
        orchestrator.reject = true;
        assertThrows(AmqpRejectAndDontRequeueException.class, () -> consumer.receive("{}"));
    }

    private static class TestOrchestrator extends JiraTicketOrchestrator {
        private boolean reject;
        TestOrchestrator() { super(null, null, null, null, null, null, null, null, null); }
        @Override public void process(com.example.jiraticketservice.event.SupportEscalationRequestedEvent event) {
            if (reject) throw new InvalidEscalationEventException("eventId is required");
        }
    }
}
