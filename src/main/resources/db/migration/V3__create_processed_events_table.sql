CREATE TABLE processed_events (
    event_id VARCHAR(150) PRIMARY KEY,
    escalation_id UUID NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_processed_events_escalation_id ON processed_events(escalation_id);
