package com.example.apischeduler.repository;
import com.example.apischeduler.entity.ApiExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiExecutionLogRepository extends JpaRepository<ApiExecutionLog, Long> {
}
