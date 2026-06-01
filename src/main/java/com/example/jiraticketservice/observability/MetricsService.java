package com.example.jiraticketservice.observability;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;

@Service
public class MetricsService {
    private final Counter consumed, created, failed, duplicate, invalid;
    private final Timer jiraLatency, processingLatency;
    public MetricsService(MeterRegistry registry) {
        consumed = registry.counter("jira_ticket_events_consumed_total");
        created = registry.counter("jira_ticket_created_total");
        failed = registry.counter("jira_ticket_failed_total");
        duplicate = registry.counter("jira_ticket_duplicate_ignored_total");
        invalid = registry.counter("jira_ticket_invalid_event_total");
        jiraLatency = registry.timer("jira_api_latency");
        processingLatency = registry.timer("queue_message_processing_latency");
    }
    public void incrementConsumed() { consumed.increment(); }
    public void incrementCreated() { created.increment(); }
    public void incrementFailed() { failed.increment(); }
    public void incrementDuplicateIgnored() { duplicate.increment(); }
    public void incrementInvalid() { invalid.increment(); }
    public <T> T timeJira(Supplier<T> action) { return jiraLatency.record(action); }
    public Timer.Sample startProcessing() { return Timer.start(); }
    public void stopProcessing(Timer.Sample sample) { sample.stop(processingLatency); }
}
