package com.example.jiraticketservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
public class ProcessedEventEntity {
    @Id @Column(name = "event_id") private String eventId;
    @Column(name = "escalation_id", nullable = false) private UUID escalationId;
    @Column(name = "processed_at", nullable = false) private LocalDateTime processedAt = LocalDateTime.now();
    public ProcessedEventEntity() {}
    public ProcessedEventEntity(String eventId, UUID escalationId) { this.eventId = eventId; this.escalationId = escalationId; }
}
