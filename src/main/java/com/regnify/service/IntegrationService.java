// src/main/java/com/regnify/service/IntegrationService.java
package com.regnify.service;

import com.regnify.dto.request.IntegrationConfigRequest;
import com.regnify.dto.response.IntegrationConfigResponse;
import com.regnify.model.IntegrationConfig;
import com.regnify.repository.IntegrationConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationService {
    
    private final IntegrationConfigRepository integrationConfigRepository;
    private final AuditService auditService;
    private final RestTemplate restTemplate;
    
    @Transactional(readOnly = true)
    public List<IntegrationConfigResponse> getAllConfigs() {
        List<IntegrationConfig> configs = integrationConfigRepository.findAll();
        return configs.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public IntegrationConfigResponse getConfigById(Long id) {
        IntegrationConfig config = integrationConfigRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Integration configuration not found"));
        return mapToResponse(config);
    }
    
    @Transactional(readOnly = true)
    public IntegrationConfigResponse getConfigByProviderName(String providerName) {
        IntegrationConfig config = integrationConfigRepository.findByServiceProviderName(providerName)
            .orElseThrow(() -> new EntityNotFoundException("Integration configuration not found"));
        return mapToResponse(config);
    }
    
    @Transactional
    public IntegrationConfigResponse createConfig(IntegrationConfigRequest request, String createdBy) {
        if (integrationConfigRepository.existsByServiceProviderName(request.getServiceProviderName())) {
            throw new RuntimeException("Service provider configuration already exists");
        }
        
        IntegrationConfig config = new IntegrationConfig();
        updateConfigFromRequest(config, request);
        config.setCreatedBy(createdBy);
        config.setUpdatedBy(createdBy);
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());
        
        IntegrationConfig savedConfig = integrationConfigRepository.save(config);
        
        auditService.logIntegrationConfigCreate(createdBy, savedConfig.getId(), 
            savedConfig.getServiceProviderName());
        
        return mapToResponse(savedConfig);
    }
    
    @Transactional
    public IntegrationConfigResponse updateConfig(Long id, IntegrationConfigRequest request, String updatedBy) {
        IntegrationConfig config = integrationConfigRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Integration configuration not found"));
        
        updateConfigFromRequest(config, request);
        config.setUpdatedBy(updatedBy);
        config.setUpdatedAt(LocalDateTime.now());
        
        IntegrationConfig updatedConfig = integrationConfigRepository.save(config);
        
        auditService.logIntegrationConfigUpdate(updatedBy, config.getId(), 
            config.getServiceProviderName());
        
        return mapToResponse(updatedConfig);
    }
    
    @Transactional
    public void deleteConfig(Long id, String deletedBy) {
        IntegrationConfig config = integrationConfigRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Integration configuration not found"));
        
        config.setIsActive(false);
        config.setUpdatedBy(deletedBy);
        config.setUpdatedAt(LocalDateTime.now());
        integrationConfigRepository.save(config);
        
        auditService.logIntegrationConfigDelete(deletedBy, config.getId(), 
            config.getServiceProviderName());
    }
    
    @Transactional
    public IntegrationConfigResponse toggleConfigStatus(Long id, String performedBy) {
        IntegrationConfig config = integrationConfigRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Integration configuration not found"));
        
        boolean newStatus = !config.getIsActive();
        config.setIsActive(newStatus);
        config.setUpdatedBy(performedBy);
        config.setUpdatedAt(LocalDateTime.now());
        
        IntegrationConfig updatedConfig = integrationConfigRepository.save(config);
        
        auditService.logIntegrationConfigStatusChange(performedBy, config.getId(), 
            config.getServiceProviderName(), newStatus);
        
        return mapToResponse(updatedConfig);
    }
    
    @Transactional
    public IntegrationConfigResponse generateCredentials(Long id, String performedBy) {
        IntegrationConfig config = integrationConfigRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Integration configuration not found"));
        
        String clientKey = "CLIENT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String clientSecret = UUID.randomUUID().toString().replace("-", "");
        
        config.setClientKey(clientKey);
        config.setClientSecret(clientSecret);
        config.setUpdatedBy(performedBy);
        config.setUpdatedAt(LocalDateTime.now());
        
        IntegrationConfig updatedConfig = integrationConfigRepository.save(config);
        
        auditService.logCredentialsGenerate(performedBy, config.getId(), 
            config.getServiceProviderName());
        
        return mapToResponse(updatedConfig);
    }
    
    @Transactional
    public String testConnection(Long id, String performedBy) {
        IntegrationConfig config = integrationConfigRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Integration configuration not found"));
        
        try {
            // Test send endpoint
            String sendResult = testEndpoint(config.getSendEndpointUrl(), config);
            
            // Test fetch endpoint
            String fetchResult = testEndpoint(config.getFetchEndpointUrl(), config);
            
            config.setSyncStatus("CONNECTED");
            config.setSyncErrors(null);
            config.setUpdatedBy(performedBy);
            config.setUpdatedAt(LocalDateTime.now());
            integrationConfigRepository.save(config);
            
            auditService.logConnectionTest(performedBy, config.getId(), 
                config.getServiceProviderName(), true, "Connection test successful");
            
            return "Connection test successful. Send endpoint: " + sendResult + 
                   ", Fetch endpoint: " + fetchResult;
            
        } catch (Exception e) {
            config.setSyncStatus("DISCONNECTED");
            config.setSyncErrors(e.getMessage());
            config.setUpdatedBy(performedBy);
            config.setUpdatedAt(LocalDateTime.now());
            integrationConfigRepository.save(config);
            
            auditService.logConnectionTest(performedBy, config.getId(), 
                config.getServiceProviderName(), false, e.getMessage());
            
            throw new RuntimeException("Connection test failed: " + e.getMessage());
        }
    }
    
    @Transactional
    public String fetchStatus(Long id, String performedBy) {
        IntegrationConfig config = integrationConfigRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Integration configuration not found"));
        
        try {
            String authHeader = createAuthHeader(config);
            
            // Make API call to fetch status
            // This is a simplified example
            String response = restTemplate.getForObject(config.getFetchEndpointUrl(), String.class);
            
            config.setLastSyncAt(LocalDateTime.now());
            config.setSyncStatus("SYNC_SUCCESS");
            config.setSyncErrors(null);
            config.setUpdatedBy(performedBy);
            config.setUpdatedAt(LocalDateTime.now());
            integrationConfigRepository.save(config);
            
            auditService.logStatusFetch(performedBy, config.getId(), 
                config.getServiceProviderName(), true, "Status fetch successful");
            
            return "Status fetch successful: " + response;
            
        } catch (Exception e) {
            config.setSyncStatus("SYNC_FAILED");
            config.setSyncErrors(e.getMessage());
            config.setUpdatedBy(performedBy);
            config.setUpdatedAt(LocalDateTime.now());
            integrationConfigRepository.save(config);
            
            auditService.logStatusFetch(performedBy, config.getId(), 
                config.getServiceProviderName(), false, e.getMessage());
            
            throw new RuntimeException("Failed to fetch status: " + e.getMessage());
        }
    }
    
    @Scheduled(cron = "0 0 9 * * *") // Daily at 9:00 AM
    @Transactional
    public void sendScheduledInvoices() {
        List<IntegrationConfig> configs = integrationConfigRepository.findActiveScheduledConfigs();
        
        for (IntegrationConfig config : configs) {
            if (config.getEnableDailySending() && config.getIsActive()) {
                try {
                    log.info("Processing scheduled invoices for: {}", config.getServiceProviderName());
                    // Implementation would fetch pending invoices and send them
                    config.setLastSyncAt(LocalDateTime.now());
                    config.setSyncStatus("SCHEDULED_SEND_SUCCESS");
                    integrationConfigRepository.save(config);
                    
                } catch (Exception e) {
                    log.error("Failed to send scheduled invoices for {}: {}", 
                        config.getServiceProviderName(), e.getMessage());
                    config.setSyncStatus("SCHEDULED_SEND_FAILED");
                    config.setSyncErrors(e.getMessage());
                    integrationConfigRepository.save(config);
                }
            }
        }
    }
    
    private void updateConfigFromRequest(IntegrationConfig config, IntegrationConfigRequest request) {
        config.setServiceProviderName(request.getServiceProviderName());
        config.setSendEndpointUrl(request.getSendEndpointUrl());
        config.setFetchEndpointUrl(request.getFetchEndpointUrl());
        config.setAuthType(request.getAuthType());
        config.setUsername(request.getUsername());
        config.setPassword(request.getPassword());
        config.setApiKey(request.getApiKey());
        config.setEnableDailySending(request.getEnableDailySending());
        config.setSendingTime(request.getSendingTime());
        config.setFrequency(request.getFrequency());
        config.setIsActive(request.getIsActive());
    }
    
    private String testEndpoint(String endpointUrl, IntegrationConfig config) {
        try {
            String authHeader = createAuthHeader(config);
            
            // Simple GET request to test connectivity
            restTemplate.getForObject(endpointUrl, String.class);
            return "OK";
            
        } catch (Exception e) {
            throw new RuntimeException("Endpoint test failed: " + e.getMessage());
        }
    }
    
    private String createAuthHeader(IntegrationConfig config) {
        switch (config.getAuthType()) {
            case BASIC:
                String auth = config.getUsername() + ":" + config.getPassword();
                return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
                
            case API_KEY:
                return "ApiKey " + config.getApiKey();
                
            case BEARER_TOKEN:
                return "Bearer " + config.getAccessToken();
                
            case OAUTH2:
                // OAuth2 implementation would be more complex
                return "Bearer " + config.getAccessToken();
                
            default:
                return "";
        }
    }
    
    private IntegrationConfigResponse mapToResponse(IntegrationConfig config) {
        return new IntegrationConfigResponse(
            config.getId(),
            config.getServiceProviderName(),
            config.getSendEndpointUrl(),
            config.getFetchEndpointUrl(),
            config.getAuthType(),
            config.getEnableDailySending(),
            config.getSendingTime(),
            config.getFrequency(),
            config.getIsActive(),
            config.getSyncStatus(),
            config.getLastSyncAt(),
            config.getCreatedAt(),
            config.getUpdatedAt()
        );
    }
}