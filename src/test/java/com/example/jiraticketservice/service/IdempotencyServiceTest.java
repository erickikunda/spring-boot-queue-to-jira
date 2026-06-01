package com.example.jiraticketservice.service;
import com.example.jiraticketservice.repository.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class IdempotencyServiceTest {
    @Test void delegatesProcessedCheck() {
        var repository = mock(ProcessedEventRepository.class); when(repository.existsById("evt")).thenReturn(true);
        assertTrue(new IdempotencyService(repository).hasProcessed("evt"));
    }
}
