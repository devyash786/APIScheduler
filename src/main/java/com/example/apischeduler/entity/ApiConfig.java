
package com.example.apischeduler.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_config")
public class ApiConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String httpMethod;
    @Column(name = "\"interval\"")
    private Long interval; // In milliseconds
    private LocalDateTime lastExecutionTime;
    private LocalDateTime nextExecutionTime;

    public void updateExecutionTimes() {
        lastExecutionTime = LocalDateTime.now();
        nextExecutionTime = lastExecutionTime.plusNanos(interval * 1_000_000);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public LocalDateTime getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(LocalDateTime lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public LocalDateTime getNextExecutionTime() {
        return nextExecutionTime;
    }

    public void setNextExecutionTime(LocalDateTime nextExecutionTime) {
        this.nextExecutionTime = nextExecutionTime;
    }


}
