package com.example.jiraticketservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jira_ticket_attempts")
public class JiraTicketAttemptEntity {
    @Id private UUID id;
    @Column(name = "escalation_id", nullable = false) private UUID escalationId;
    @Column(name = "event_id", nullable = false) private String eventId;
    @Column(name = "attempt_number", nullable = false) private int attemptNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private JiraTicketAttemptStatus status;
    @Column(name = "jira_ticket_key") private String jiraTicketKey;
    @Column(name = "http_status") private Integer httpStatus;
    @Column(name = "error_message") private String errorMessage;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "request_payload", columnDefinition = "jsonb") private String requestPayload;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "response_payload", columnDefinition = "jsonb") private String responsePayload;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public JiraTicketAttemptEntity() {}
    public JiraTicketAttemptEntity(UUID escalationId, String eventId, int attemptNumber, JiraTicketAttemptStatus status) {
        this.id = UUID.randomUUID(); this.escalationId = escalationId; this.eventId = eventId;
        this.attemptNumber = attemptNumber; this.status = status;
    }
    public void setJiraTicketKey(String jiraTicketKey) { this.jiraTicketKey = jiraTicketKey; }
    public void setHttpStatus(Integer httpStatus) { this.httpStatus = httpStatus; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setRequestPayload(String requestPayload) { this.requestPayload = requestPayload; }
    public void setResponsePayload(String responsePayload) { this.responsePayload = responsePayload; }
}
