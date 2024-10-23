
package com.example.apischeduler.service;

import com.example.apischeduler.entity.ApiConfig;
import com.example.apischeduler.repository.ApiConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import com.example.apischeduler.entity.ApiExecutionLog;
import com.example.apischeduler.repository.ApiExecutionLogRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Service
public class ApiSchedulerService {

    @Autowired
    private ApiConfigRepository apiConfigRepository;

    @Autowired
    private ApiExecutionLogRepository apiExecutionLogRepository;

    private final Map<Long, Integer> failureCount = new HashMap<>();
    private static final int FAILURE_THRESHOLD = 3;

    @PostConstruct
    public void scheduleApiExecutions() {
        List<ApiConfig> apiConfigs = apiConfigRepository.findAll();
        for (ApiConfig apiConfig : apiConfigs) {
            apiConfig.updateExecutionTimes();
            apiConfigRepository.save(apiConfig);
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public String executeApi(Long apiId) {
        ApiConfig apiConfig = apiConfigRepository.findById(apiId).orElseThrow();
        RestTemplate restTemplate = new RestTemplate();

        long startTime = System.currentTimeMillis();
        try {
            String response = restTemplate.exchange(apiConfig.getUrl(), HttpMethod.resolve(apiConfig.getHttpMethod()), null, String.class).getBody();
            long responseTime = System.currentTimeMillis() - startTime;

            saveExecutionLog(apiConfig, true, responseTime, null);
            return response;
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            saveExecutionLog(apiConfig, false, responseTime, e.getMessage());

            incrementFailureCount(apiId);
            throw e; // Propagate exception for retry
        }
    }

    @Scheduled(fixedDelay = 1000) // Check every second
    public void checkAndExecuteScheduledApis() {
        List<ApiConfig> apiConfigs = apiConfigRepository.findAll();
        for (ApiConfig apiConfig : apiConfigs) {
            if (LocalDateTime.now().isAfter(apiConfig.getNextExecutionTime())) {
                executeApi(apiConfig.getId());
                apiConfig.updateExecutionTimes();
                apiConfigRepository.save(apiConfig);
            }
        }
    }

    public ApiConfig addApiConfig(ApiConfig apiConfig) {
        return apiConfigRepository.save(apiConfig);
    }

    public void deleteApiConfig(Long id) {
        apiConfigRepository.deleteById(id);
    }

    public List<ApiConfig> getAllApiConfigs() {
        return apiConfigRepository.findAll();
    }

    private void saveExecutionLog(ApiConfig apiConfig, boolean success, long responseTime, String errorDetails) {
        ApiExecutionLog log = new ApiExecutionLog();
        log.setApiConfig(apiConfig);
        log.setExecutionTime(LocalDateTime.now());
        log.setSuccess(success);
        log.setResponseTime(responseTime);
        log.setErrorDetails(errorDetails);
        apiExecutionLogRepository.save(log);
    }

    private void incrementFailureCount(Long apiId) {
        failureCount.put(apiId, failureCount.getOrDefault(apiId, 0) + 1);
        if (failureCount.get(apiId) >= FAILURE_THRESHOLD) {
            // Implement alerting logic here
            System.out.println("Alert: Continuous failures for API ID " + apiId);
        }
    }

    @Recover
    public void recover(Exception e, Long apiId) {
        System.out.println("Failed to execute API after retries: " + apiId);
    }
}
