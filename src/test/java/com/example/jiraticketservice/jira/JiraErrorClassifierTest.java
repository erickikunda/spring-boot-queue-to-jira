package com.example.jiraticketservice.jira;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class JiraErrorClassifierTest {
    private final JiraErrorClassifier classifier = new JiraErrorClassifier();
    @Test void classifiesTransientErrors() { assertTrue(classifier.isRetryable(429)); assertTrue(classifier.isRetryable(503)); }
    @Test void classifiesPermanentErrors() { assertFalse(classifier.isRetryable(400)); assertFalse(classifier.isRetryable(404)); }
}
