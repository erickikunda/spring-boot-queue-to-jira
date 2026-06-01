# Jira Ticket Service

Spring Boot microservice that consumes low-confidence chatbot escalation events from RabbitMQ and creates Jira support tickets.

## Local run

```bash
docker compose up --build
```

Health is exposed at `http://localhost:8082/actuator/health`. RabbitMQ management is at `http://localhost:15672`.

The service expects an existing row in `escalations` before an event is published. Jira credentials are configured through `JIRA_BASE_URL`, `JIRA_EMAIL`, and `JIRA_API_TOKEN`.
