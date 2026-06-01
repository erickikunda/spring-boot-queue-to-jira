package com.example.jiraticketservice.service;

import com.example.jiraticketservice.entity.*;
import com.example.jiraticketservice.event.SupportEscalationRequestedEvent;
import com.example.jiraticketservice.exception.*;
import com.example.jiraticketservice.jira.*;
import com.example.jiraticketservice.repository.JiraTicketAttemptRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JiraTicketAttemptService {
    private final JiraTicketAttemptRepository repository;
    private final ObjectMapper objectMapper;
    public JiraTicketAttemptService(JiraTicketAttemptRepository repository, ObjectMapper objectMapper) {
        this.repository = repository; this.objectMapper = objectMapper;
    }
    @Transactional public void recordSuccess(SupportEscalationRequestedEvent e, JiraCreateIssueRequest request, JiraCreateIssueResponse response) {
        var attempt = create(e, request, JiraTicketAttemptStatus.SUCCESS);
        attempt.setJiraTicketKey(response.key()); attempt.setResponsePayload(json(response)); repository.save(attempt);
    }
    @Transactional public void recordRetryableFailure(SupportEscalationRequestedEvent e, JiraCreateIssueRequest request, RetryableJiraException error) {
        var attempt = create(e, request, JiraTicketAttemptStatus.FAILED_RETRYABLE);
        attempt.setHttpStatus(error.getHttpStatus()); attempt.setErrorMessage(error.getMessage()); repository.save(attempt);
    }
    @Transactional public void recordNonRetryableFailure(SupportEscalationRequestedEvent e, JiraCreateIssueRequest request, NonRetryableJiraException error) {
        var attempt = create(e, request, JiraTicketAttemptStatus.FAILED_NON_RETRYABLE);
        attempt.setHttpStatus(error.getHttpStatus()); attempt.setErrorMessage(error.getMessage()); repository.save(attempt);
    }
    private JiraTicketAttemptEntity create(SupportEscalationRequestedEvent e, JiraCreateIssueRequest request, JiraTicketAttemptStatus status) {
        var attempt = new JiraTicketAttemptEntity(e.escalationId(), e.eventId(), (int) repository.countByEscalationId(e.escalationId()) + 1, status);
        attempt.setRequestPayload(json(request));
        return attempt;
    }
    private String json(Object value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (JsonProcessingException ex) { throw new IllegalStateException("Could not serialize Jira audit payload", ex); }
    }
}
