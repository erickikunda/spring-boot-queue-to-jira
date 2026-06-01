package com.example.jiraticketservice.service;
import com.example.jiraticketservice.entity.*;
import com.example.jiraticketservice.repository.EscalationRepository;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class EscalationServiceTest {
    @Test void returnsClaimedEscalation() {
        var repository = mock(EscalationRepository.class); var id = UUID.randomUUID();
        var entity = new EscalationEntity(id, UUID.randomUUID(), EscalationStatus.PENDING);
        when(repository.claimForProcessing(id)).thenReturn(1); when(repository.findById(id)).thenReturn(Optional.of(entity));
        assertTrue(new EscalationService(repository).claimForProcessing(id).isPresent());
    }
    @Test void returnsEmptyWhenClaimFails() {
        var repository = mock(EscalationRepository.class); var id = UUID.randomUUID();
        when(repository.claimForProcessing(id)).thenReturn(0);
        assertTrue(new EscalationService(repository).claimForProcessing(id).isEmpty());
    }
}
