package com.example.jiraticketservice.jira;

import com.example.jiraticketservice.exception.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

@Component
public class JiraClient {
    private final WebClient webClient;
    private final JiraErrorClassifier classifier;
    public JiraClient(WebClient jiraWebClient, JiraErrorClassifier classifier) {
        this.webClient = jiraWebClient; this.classifier = classifier;
    }
    @Retry(name = "jiraApi")
    @CircuitBreaker(name = "jiraApi")
    public JiraCreateIssueResponse createIssue(JiraCreateIssueRequest request) {
        try {
            return webClient.post().uri("/rest/api/3/issue").bodyValue(request).retrieve()
                    .onStatus(s -> s.isError(), r -> r.bodyToMono(String.class).defaultIfEmpty("")
                            .flatMap(body -> Mono.error(toException(r.statusCode().value(), body))))
                    .bodyToMono(JiraCreateIssueResponse.class).block();
        } catch (NonRetryableJiraException | RetryableJiraException e) {
            throw e;
        } catch (WebClientException e) {
            throw new RetryableJiraException("Jira network failure", e);
        }
    }
    private RuntimeException toException(int status, String body) {
        String message = "Jira returned HTTP " + status + (body.isBlank() ? "" : ": " + body);
        return classifier.isRetryable(status) ? new RetryableJiraException(message, status)
                : new NonRetryableJiraException(message, status);
    }
}
