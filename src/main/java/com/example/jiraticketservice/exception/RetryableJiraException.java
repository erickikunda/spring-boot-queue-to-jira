package com.example.jiraticketservice.exception;
public class RetryableJiraException extends RuntimeException {
    private final Integer httpStatus;
    public RetryableJiraException(String m, Integer httpStatus) { super(m); this.httpStatus = httpStatus; }
    public RetryableJiraException(String m, Throwable cause) { super(m, cause); this.httpStatus = null; }
    public Integer getHttpStatus() { return httpStatus; }
}
