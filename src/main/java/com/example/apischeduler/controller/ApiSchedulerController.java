
package com.example.apischeduler.controller;

import com.example.apischeduler.entity.ApiConfig;
import com.example.apischeduler.service.ApiSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduler")
public class ApiSchedulerController {

    @Autowired
    private ApiSchedulerService apiSchedulerService;

    @GetMapping("/all")
    public List<ApiConfig> getAllApis() {
        return apiSchedulerService.getAllApiConfigs();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addApi(@RequestBody ApiConfig apiConfig) {
        apiSchedulerService.addApi(apiConfig);
        return ResponseEntity.ok("API Added Successfully");
    }

    @PostMapping("/trigger/{id}")
    public ResponseEntity<String> triggerApi(@PathVariable Long id) {
        apiSchedulerService.triggerApi(id);
        return ResponseEntity.ok("API Triggered Successfully");
    }
}
