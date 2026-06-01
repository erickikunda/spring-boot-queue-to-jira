package com.example.jiraticketservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id private UUID id;
    @Column(name = "conversation_id", nullable = false) private UUID conversationId;
    @Column(nullable = false) private String role;
    @Column(nullable = false) private String content;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private String metadata;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public MessageEntity() {}
    public MessageEntity(UUID id, UUID conversationId, String role, String content, LocalDateTime createdAt) {
        this.id = id; this.conversationId = conversationId; this.role = role; this.content = content; this.createdAt = createdAt;
    }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
