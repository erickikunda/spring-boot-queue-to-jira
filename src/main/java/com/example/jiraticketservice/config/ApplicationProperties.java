package com.example.jiraticketservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record ApplicationProperties(Jira jira, Queue queue) {
    public record Jira(String baseUrl, String email, String apiToken, String projectKey, String issueType) {}
    public record Queue(String exchange, String escalationQueue, String deadLetterQueue, String routingKey,
                        String deadLetterRoutingKey) {}
}
