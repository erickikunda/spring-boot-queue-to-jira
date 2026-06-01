package com.example.jiraticketservice.repository;
import com.example.jiraticketservice.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, String> {}
