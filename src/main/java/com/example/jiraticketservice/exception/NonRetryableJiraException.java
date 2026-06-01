package com.example.jiraticketservice.exception;
public class NonRetryableJiraException extends RuntimeException {
    private final Integer httpStatus;
    public NonRetryableJiraException(String m, Integer httpStatus) { super(m); this.httpStatus = httpStatus; }
    public Integer getHttpStatus() { return httpStatus; }
}
