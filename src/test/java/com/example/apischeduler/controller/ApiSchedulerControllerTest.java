package com.example.apischeduler.controller;

import com.example.apischeduler.entity.ApiConfig;
import com.example.apischeduler.service.ApiSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiSchedulerControllerTest {

    @Mock
    private ApiSchedulerService apiSchedulerService;

    @InjectMocks
    private ApiSchedulerController apiSchedulerController;

    private ApiConfig apiConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        apiConfig = new ApiConfig();
        apiConfig.setId(1L);
        apiConfig.setUrl("http://example.com/api");
        apiConfig.setHttpMethod("GET");
    }

    @Test
    void testAddApiConfig() {
        when(apiSchedulerService.addApiConfig(any(ApiConfig.class))).thenReturn(apiConfig);

        ApiConfig result = apiSchedulerController.addApiConfig(apiConfig);

        assertNotNull(result);
        assertEquals(apiConfig.getId(), result.getId());
        verify(apiSchedulerService, times(1)).addApiConfig(apiConfig);
    }

    @Test
    void testDeleteApiConfig() {
        doNothing().when(apiSchedulerService).deleteApiConfig(1L);

        apiSchedulerController.deleteApiConfig(1L);

        verify(apiSchedulerService, times(1)).deleteApiConfig(1L);
    }

    @Test
    void testExecuteApi() {
        when(apiSchedulerService.executeApi(1L)).thenReturn("Success");

        String response = apiSchedulerController.executeApi(1L);

        assertEquals("Success", response);
        verify(apiSchedulerService, times(1)).executeApi(1L);
    }

    @Test
    void testGetAllApiConfigs() {
        List<ApiConfig> apiConfigs = new ArrayList<>();
        apiConfigs.add(apiConfig);
        when(apiSchedulerService.getAllApiConfigs()).thenReturn(apiConfigs);

        List<ApiConfig> result = apiSchedulerController.getAllApiConfigs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(apiConfig.getId(), result.get(0).getId());
        verify(apiSchedulerService, times(1)).getAllApiConfigs();
    }
}
