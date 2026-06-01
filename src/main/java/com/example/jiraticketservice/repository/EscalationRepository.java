package com.example.jiraticketservice.repository;

import com.example.jiraticketservice.entity.EscalationEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface EscalationRepository extends JpaRepository<EscalationEntity, UUID> {
    @Modifying
    @Query("""
        UPDATE EscalationEntity e SET e.status = com.example.jiraticketservice.entity.EscalationStatus.PROCESSING,
        e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id
        AND e.status IN (com.example.jiraticketservice.entity.EscalationStatus.PENDING,
        com.example.jiraticketservice.entity.EscalationStatus.QUEUED,
        com.example.jiraticketservice.entity.EscalationStatus.FAILED) AND e.jiraTicketKey IS NULL
        """)
    int claimForProcessing(@Param("id") UUID id);
}
