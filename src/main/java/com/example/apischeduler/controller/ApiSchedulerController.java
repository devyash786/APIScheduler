
package com.example.apischeduler.controller;

import com.example.apischeduler.entity.ApiConfig;
import com.example.apischeduler.service.ApiSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduler")
public class ApiSchedulerController {
    @Autowired
    private ApiSchedulerService apiSchedulerService;

    @PostMapping("/add")
    public ApiConfig addApiConfig(@RequestBody ApiConfig apiConfig) {
        return apiSchedulerService.addApiConfig(apiConfig);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteApiConfig(@PathVariable Long id) {
        apiSchedulerService.deleteApiConfig(id);
    }

    @PostMapping("/execute/{id}")
    public String executeApi(@PathVariable Long id) {
        return apiSchedulerService.executeApi(id);
    }

    @GetMapping("/all")
    public List<ApiConfig> getAllApiConfigs() {
        return apiSchedulerService.getAllApiConfigs();
    }
}
