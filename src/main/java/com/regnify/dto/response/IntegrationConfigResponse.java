// src/main/java/com/regnify/dto/response/IntegrationConfigResponse.java
package com.regnify.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.regnify.model.IntegrationConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationConfigResponse {
    
    private Long id;
    private String serviceProviderName;
    private String sendEndpointUrl;
    private String fetchEndpointUrl;
    private IntegrationConfig.AuthType authType;
    private Boolean enableDailySending;
    private String sendingTime;
    private IntegrationConfig.Frequency frequency;
    private Boolean isActive;
    private String syncStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSyncAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}