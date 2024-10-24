package com.example.apischeduler.service;

import com.example.apischeduler.entity.ApiConfig;
import com.example.apischeduler.entity.ApiExecutionLog;
import com.example.apischeduler.repository.ApiConfigRepository;
import com.example.apischeduler.repository.ApiExecutionLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiSchedulerServiceTest {

    @Mock
    private ApiConfigRepository apiConfigRepository;

    @Mock
    private ApiExecutionLogRepository apiExecutionLogRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ApiSchedulerService apiSchedulerService;

    private ApiConfig apiConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        apiConfig = new ApiConfig();
        apiConfig.setId(1L);
        apiConfig.setUrl("https://jsonplaceholder.typicode.com/todos/1");
        apiConfig.setHttpMethod(HttpMethod.GET.name());
        apiConfig.setNextExecutionTime(LocalDateTime.now().minusMinutes(1)); // Set to past for testing
    }

    @Test
    void testExecuteApi_Success() {
        when(apiConfigRepository.findById(1L)).thenReturn(Optional.of(apiConfig));
        RestTemplate restTemplateMock = mock(RestTemplate.class);
        String expectedResponse = "Success";

        // Mock the RestTemplate behavior
        when(restTemplateMock.exchange(eq(apiConfig.getUrl()), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        String response = apiSchedulerService.executeApi(1L);

        assertNotEquals(expectedResponse, response);
        verify(apiExecutionLogRepository, times(1)).save(any(ApiExecutionLog.class));
    }

    @Test
    void testExecuteApi_Failure() {
        apiConfig.setUrl("http://example.com/api");
        when(apiConfigRepository.findById(1L)).thenReturn(Optional.of(apiConfig));
        RestTemplate restTemplateMock = mock(RestTemplate.class);

        // Mock the RestTemplate to throw an exception
        when(restTemplateMock.exchange(eq(apiConfig.getUrl()), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenThrow(new RuntimeException("API call failed"));

        assertThrows(RuntimeException.class, () -> apiSchedulerService.executeApi(1L));
        verify(apiExecutionLogRepository, times(1)).save(any(ApiExecutionLog.class));
    }

    @Test
    void testCheckAndExecuteScheduledApis() {
        apiConfig = new ApiConfig();
        apiConfig.setId(1l);apiConfig.setInterval(400l);
        apiConfig.setUrl("https://jsonplaceholder.typicode.com/todos/1");
        apiConfig.setHttpMethod(HttpMethod.GET.name());
        apiConfig.setNextExecutionTime(LocalDateTime.now().minusMinutes(5));
        when(apiConfigRepository.findAll()).thenReturn(Arrays.asList(apiConfig));
        Optional<ApiConfig> optionalApiConfig = Optional.of(apiConfig);
        when(apiConfigRepository.findById(apiConfig.getId())).thenReturn(optionalApiConfig);
        String expectedResponse = "Success";
        // Mock the response from RestTemplate
        ResponseEntity<String> responseEntity = ResponseEntity.ok(expectedResponse);

        when(restTemplate.exchange(
                any(String.class),   // Mocking the URL parameter
                any(HttpMethod.class), // Mocking the HttpMethod
                any(HttpEntity.class),  // Mocking the request entity (null in your case)
                any(Class.class)       // Mocking the response type
        )).thenReturn(responseEntity);

        apiSchedulerService.checkAndExecuteScheduledApis();

        verify(apiConfigRepository, times(1)).save(apiConfig);
        verify(apiExecutionLogRepository, times(1)).save(any(ApiExecutionLog.class));
    }

    @Test
    void testAddApiConfig() {
        when(apiConfigRepository.save(apiConfig)).thenReturn(apiConfig);

        ApiConfig result = apiSchedulerService.addApiConfig(apiConfig);

        assertEquals(apiConfig, result);
        verify(apiConfigRepository, times(1)).save(apiConfig);
    }

    @Test
    void testDeleteApiConfig() {
        doNothing().when(apiConfigRepository).deleteById(1L);

        apiSchedulerService.deleteApiConfig(1L);

        verify(apiConfigRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetAllApiConfigs() {
        apiConfig = new ApiConfig();
        when(apiConfigRepository.findAll()).thenReturn(Arrays.asList(apiConfig));

        List<ApiConfig> result = apiSchedulerService.getAllApiConfigs();

        assertFalse(result.isEmpty());
        verify(apiConfigRepository, times(1)).findAll();
    }
}
