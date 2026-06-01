package com.example.jiraticketservice.jira;
import java.util.List;
public record JiraCreateIssueRequest(Fields fields) {
    public record Fields(Project project, String summary, String description, IssueType issuetype, Priority priority, List<String> labels) {}
    public record Project(String key) {}
    public record IssueType(String name) {}
    public record Priority(String name) {}
}
