package com.example.jiraticketservice.service;

import com.example.jiraticketservice.config.ApplicationProperties;
import com.example.jiraticketservice.event.*;
import com.example.jiraticketservice.jira.JiraCreateIssueRequest;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class JiraIssueBuilder {
    private final ApplicationProperties properties;
    public JiraIssueBuilder(ApplicationProperties properties) { this.properties = properties; }
    public JiraCreateIssueRequest build(SupportEscalationRequestedEvent event, ConversationContextService.ConversationContext context) {
        var labels = new ArrayList<>(List.of("chatbot-escalation", "low-confidence"));
        Object product = event.metadata() == null ? null : event.metadata().get("product");
        if (product != null && !sanitizeLabel(product.toString()).isBlank()) labels.add(sanitizeLabel(product.toString()));
        var fields = new JiraCreateIssueRequest.Fields(new JiraCreateIssueRequest.Project(properties.jira().projectKey()),
                summary(event.question()), description(event, context), new JiraCreateIssueRequest.IssueType(properties.jira().issueType()),
                new JiraCreateIssueRequest.Priority(priority(event)), labels);
        return new JiraCreateIssueRequest(fields);
    }
    String summary(String question) {
        String shortened = question.length() <= 80 ? question : question.substring(0, 80);
        return "Chatbot escalation: " + shortened;
    }
    String priority(SupportEscalationRequestedEvent event) {
        Object hint = event.metadata() == null ? null : event.metadata().get("priorityHint");
        return "high".equalsIgnoreCase(String.valueOf(hint)) || event.confidenceScore() < .30 ? "High" : "Medium";
    }
    String description(SupportEscalationRequestedEvent e, ConversationContextService.ConversationContext context) {
        var b = new StringBuilder();
        section(b, "Customer Question", e.question());
        section(b, "Confidence Score", String.valueOf(e.confidenceScore()));
        section(b, "Confidence Reason", value(e.confidenceReason()));
        section(b, "Generated Answer", e.generatedAnswer() == null ? "No generated answer was returned because confidence was below the threshold." : e.generatedAnswer());
        section(b, "Customer", value(e.customerName()) + "\n" + value(e.customerEmail()));
        section(b, "Conversation ID", e.conversationId().toString());
        section(b, "Escalation ID", e.escalationId().toString());
        b.append("Retrieved Sources:\n");
        var sources = e.retrievedSources() == null ? List.<RetrievedSource>of() : e.retrievedSources();
        for (int i = 0; i < sources.size(); i++) b.append(i + 1).append(". ").append(value(sources.get(i).title())).append("\n   ").append(value(sources.get(i).url())).append("\n");
        b.append("\nRecent Conversation:\n");
        context.messages().forEach(m -> b.append(capitalize(m.getRole())).append(": ").append(m.getContent()).append("\n"));
        return b.toString().stripTrailing();
    }
    private void section(StringBuilder b, String name, String value) { b.append(name).append(":\n").append(value).append("\n\n"); }
    private String value(String value) { return value == null || value.isBlank() ? "Not provided" : value; }
    private String sanitizeLabel(String value) { return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]+", "-").replaceAll("^-|-$", ""); }
    private String capitalize(String value) { return value == null || value.isBlank() ? "Unknown" : Character.toUpperCase(value.charAt(0)) + value.substring(1); }
}
