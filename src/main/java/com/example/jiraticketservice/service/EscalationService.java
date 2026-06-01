package com.example.jiraticketservice.service;

import com.example.jiraticketservice.entity.*;
import com.example.jiraticketservice.repository.EscalationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EscalationService {
    private final EscalationRepository repository;
    public EscalationService(EscalationRepository repository) { this.repository = repository; }
    @Transactional
    public Optional<EscalationEntity> claimForProcessing(UUID id) {
        if (repository.claimForProcessing(id) == 0) return Optional.empty();
        return repository.findById(id);
    }
    @Transactional public void markTicketCreated(UUID id, String key, String url) {
        update(id, EscalationStatus.TICKET_CREATED, key, url, null);
    }
    @Transactional public void markFailed(UUID id, String error) { update(id, EscalationStatus.FAILED, null, null, error); }
    @Transactional public void markInvalid(UUID id, String error) { update(id, EscalationStatus.INVALID_EVENT, null, null, error); }
    private void update(UUID id, EscalationStatus status, String key, String url, String error) {
        repository.findById(id).ifPresent(e -> {
            e.setStatus(status); e.setUpdatedAt(LocalDateTime.now()); e.setErrorMessage(error);
            if (key != null) e.setJiraTicketKey(key);
            if (url != null) e.setJiraTicketUrl(url);
        });
    }
}
