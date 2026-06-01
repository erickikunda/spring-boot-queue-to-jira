package com.example.jiraticketservice.repository;
import com.example.jiraticketservice.entity.JiraTicketAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JiraTicketAttemptRepository extends JpaRepository<JiraTicketAttemptEntity, UUID> {
    long countByEscalationId(UUID escalationId);
}
