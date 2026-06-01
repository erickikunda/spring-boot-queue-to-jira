CREATE TABLE escalations (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    user_message_id UUID,
    status VARCHAR(50) NOT NULL,
    confidence_score NUMERIC(5,4),
    confidence_reason TEXT,
    jira_ticket_key VARCHAR(100),
    jira_ticket_url TEXT,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_escalations_conversation_id ON escalations(conversation_id);
CREATE INDEX idx_escalations_status ON escalations(status);
