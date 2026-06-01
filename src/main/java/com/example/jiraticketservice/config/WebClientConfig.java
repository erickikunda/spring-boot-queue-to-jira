package com.example.jiraticketservice.config;

import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean WebClient jiraWebClient(ApplicationProperties p) {
        return WebClient.builder().baseUrl(p.jira().baseUrl())
                .defaultHeaders(h -> h.setBasicAuth(p.jira().email(), p.jira().apiToken())).build();
    }
}
