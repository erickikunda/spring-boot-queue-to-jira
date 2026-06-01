package com.example.jiraticketservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JiraTicketServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JiraTicketServiceApplication.class, args);
    }
}
