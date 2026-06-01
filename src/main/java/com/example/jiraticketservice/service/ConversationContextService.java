package com.example.jiraticketservice.service;

import com.example.jiraticketservice.entity.MessageEntity;
import com.example.jiraticketservice.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ConversationContextService {
    private final MessageRepository repository;
    public ConversationContextService(MessageRepository repository) { this.repository = repository; }
    public ConversationContext loadContext(UUID conversationId) {
        var messages = new ArrayList<>(repository.findTop20ByConversationIdOrderByCreatedAtDesc(conversationId));
        Collections.reverse(messages);
        return new ConversationContext(messages);
    }
    public record ConversationContext(List<MessageEntity> messages) {}
}
