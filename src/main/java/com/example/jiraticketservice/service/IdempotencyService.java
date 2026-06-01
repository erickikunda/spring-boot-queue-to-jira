package com.example.jiraticketservice.service;

import com.example.jiraticketservice.entity.ProcessedEventEntity;
import com.example.jiraticketservice.repository.ProcessedEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class IdempotencyService {
    private final ProcessedEventRepository repository;
    public IdempotencyService(ProcessedEventRepository repository) { this.repository = repository; }
    public boolean hasProcessed(String eventId) { return repository.existsById(eventId); }
    @Transactional public void markProcessed(String eventId, UUID escalationId) {
        repository.save(new ProcessedEventEntity(eventId, escalationId));
    }
}
