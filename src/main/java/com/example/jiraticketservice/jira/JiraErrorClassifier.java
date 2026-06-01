package com.example.jiraticketservice.jira;
import org.springframework.stereotype.Component;
@Component
public class JiraErrorClassifier {
    public boolean isRetryable(int status) { return status == 429 || status >= 500 && status <= 599; }
}
