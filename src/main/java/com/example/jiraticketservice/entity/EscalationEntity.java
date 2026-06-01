package com.example.jiraticketservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "escalations")
public class EscalationEntity {
    @Id private UUID id;
    @Column(name = "conversation_id", nullable = false) private UUID conversationId;
    @Column(name = "user_message_id") private UUID userMessageId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EscalationStatus status;
    @Column(name = "confidence_score") private BigDecimal confidenceScore;
    @Column(name = "confidence_reason") private String confidenceReason;
    @Column(name = "jira_ticket_key") private String jiraTicketKey;
    @Column(name = "jira_ticket_url") private String jiraTicketUrl;
    @Column(name = "error_message") private String errorMessage;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt = LocalDateTime.now();

    public EscalationEntity() {}
    public EscalationEntity(UUID id, UUID conversationId, EscalationStatus status) {
        this.id = id; this.conversationId = conversationId; this.status = status;
    }
    public UUID getId() { return id; }
    public UUID getConversationId() { return conversationId; }
    public EscalationStatus getStatus() { return status; }
    public String getJiraTicketKey() { return jiraTicketKey; }
    public String getJiraTicketUrl() { return jiraTicketUrl; }
    public String getErrorMessage() { return errorMessage; }
    public void setStatus(EscalationStatus status) { this.status = status; }
    public void setJiraTicketKey(String jiraTicketKey) { this.jiraTicketKey = jiraTicketKey; }
    public void setJiraTicketUrl(String jiraTicketUrl) { this.jiraTicketUrl = jiraTicketUrl; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
