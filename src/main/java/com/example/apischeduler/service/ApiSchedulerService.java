
package com.example.apischeduler.service;

import com.example.apischeduler.entity.ApiConfig;
import com.example.apischeduler.repository.ApiConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Service
public class ApiSchedulerService {

    @Autowired
    private ApiConfigRepository apiConfigRepository;

    public List<ApiConfig> getAllApiConfigs() {
        return apiConfigRepository.findAll();
    }

    public void addApi(ApiConfig apiConfig) {
        apiConfigRepository.save(apiConfig);
    }

    public void triggerApi(Long id) {
        ApiConfig apiConfig = apiConfigRepository.findById(id).orElseThrow(() -> new RuntimeException("API not found"));
        executeApi(apiConfig);
    }

    @Scheduled(fixedRate = 60000)
    public void scheduleApiExecution() {
        List<ApiConfig> apiConfigs = apiConfigRepository.findAll();
        for (ApiConfig apiConfig : apiConfigs) {
            executeApi(apiConfig);
        }
    }

    private void executeApi(ApiConfig apiConfig) {
        // Logic to make API call and log execution
    }
}
