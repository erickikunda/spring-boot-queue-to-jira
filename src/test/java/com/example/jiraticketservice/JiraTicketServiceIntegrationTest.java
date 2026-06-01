package com.example.jiraticketservice;

import com.example.jiraticketservice.event.SupportEscalationRequestedEvent;
import com.example.jiraticketservice.exception.*;
import com.example.jiraticketservice.service.JiraTicketOrchestrator;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.Instant;
import java.util.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "spring.rabbitmq.listener.simple.auto-startup=false")
class JiraTicketServiceIntegrationTest {
    @Container static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container static final RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management-alpine");
    static final WireMockServer wireMock = new WireMockServer(options().dynamicPort());
    static { wireMock.start(); }

    @Autowired JiraTicketOrchestrator orchestrator;
    @Autowired JdbcTemplate jdbc;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("app.jira.base-url", wireMock::baseUrl);
    }

    @BeforeEach void reset() {
        wireMock.resetAll();
        jdbc.update("DELETE FROM jira_ticket_attempts");
        jdbc.update("DELETE FROM processed_events");
        jdbc.update("DELETE FROM escalations");
    }
    @AfterAll static void stopWireMock() { wireMock.stop(); }

    @Test void validEventCreatesTicketAndDuplicateIsIgnored() {
        var event = event();
        insertEscalation(event, null);
        wireMock.stubFor(post("/rest/api/3/issue").willReturn(okJson("{\"id\":\"10001\",\"key\":\"SUP-42\",\"self\":\"ignored\"}")));
        orchestrator.process(event);
        orchestrator.process(event);
        assertEquals("TICKET_CREATED", jdbc.queryForObject("SELECT status FROM escalations WHERE id = ?", String.class, event.escalationId()));
        assertEquals("SUP-42", jdbc.queryForObject("SELECT jira_ticket_key FROM escalations WHERE id = ?", String.class, event.escalationId()));
        assertEquals(1, jdbc.queryForObject("SELECT count(*) FROM processed_events", Integer.class));
        wireMock.verify(1, postRequestedFor(urlEqualTo("/rest/api/3/issue")));
    }

    @Test void existingTicketKeyIsIgnored() {
        var event = event();
        insertEscalation(event, "SUP-EXISTING");
        orchestrator.process(event);
        wireMock.verify(0, postRequestedFor(urlEqualTo("/rest/api/3/issue")));
    }

    @Test void jira500IsRetryableAndRecorded() {
        var event = event();
        insertEscalation(event, null);
        wireMock.stubFor(post("/rest/api/3/issue").willReturn(serverError()));
        assertThrows(RetryableJiraException.class, () -> orchestrator.process(event));
        assertEquals("FAILED_RETRYABLE", jdbc.queryForObject("SELECT status FROM jira_ticket_attempts", String.class));
    }

    @Test void jira400IsNonRetryableAndRecorded() {
        var event = event();
        insertEscalation(event, null);
        wireMock.stubFor(post("/rest/api/3/issue").willReturn(badRequest()));
        assertThrows(NonRetryableJiraException.class, () -> orchestrator.process(event));
        assertEquals("FAILED_NON_RETRYABLE", jdbc.queryForObject("SELECT status FROM jira_ticket_attempts", String.class));
    }

    private void insertEscalation(SupportEscalationRequestedEvent event, String jiraKey) {
        jdbc.update("INSERT INTO escalations(id, conversation_id, status, jira_ticket_key) VALUES (?, ?, 'PENDING', ?)",
                event.escalationId(), event.conversationId(), jiraKey);
    }
    private SupportEscalationRequestedEvent event() {
        return new SupportEscalationRequestedEvent("evt-" + UUID.randomUUID(), "support_escalation_requested", Instant.now(),
                UUID.randomUUID(), UUID.randomUUID(), "customer", "customer@example.com", "Jane", "Why are there two tax lines?",
                null, .42, "weak match", List.of(), Map.of("product", "billing"));
    }
}
