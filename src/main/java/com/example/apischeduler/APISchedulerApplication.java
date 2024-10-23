package com.example.apischeduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class APISchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(APISchedulerApplication.class, args);
    }
}
