
package com.example.apischeduler.entity;

import javax.persistence.*;

@Entity
public class ApiConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private String httpMethod;
    private Long scheduleInterval;

    // Getters and Setters
}
