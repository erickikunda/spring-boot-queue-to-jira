package com.example.jiraticketservice.repository;
import com.example.jiraticketservice.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    List<MessageEntity> findTop20ByConversationIdOrderByCreatedAtDesc(UUID conversationId);
}
