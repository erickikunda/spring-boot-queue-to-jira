CREATE TABLE jira_ticket_attempts (
    id UUID PRIMARY KEY,
    escalation_id UUID NOT NULL,
    event_id VARCHAR(150) NOT NULL,
    attempt_number INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    jira_ticket_key VARCHAR(100),
    http_status INTEGER,
    error_message TEXT,
    request_payload JSONB,
    response_payload JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_jira_ticket_attempts_escalation FOREIGN KEY (escalation_id) REFERENCES escalations(id)
);
CREATE INDEX idx_jira_ticket_attempts_escalation_id ON jira_ticket_attempts(escalation_id);
CREATE INDEX idx_jira_ticket_attempts_event_id ON jira_ticket_attempts(event_id);
