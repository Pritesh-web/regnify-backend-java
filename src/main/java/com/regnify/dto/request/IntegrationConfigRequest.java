// src/main/java/com/regnify/dto/request/IntegrationConfigRequest.java
package com.regnify.dto.request;

import com.regnify.model.IntegrationConfig;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationConfigRequest {
    
    @NotBlank(message = "Service provider name is required")
    private String serviceProviderName;
    
    @NotBlank(message = "Send endpoint URL is required")
    private String sendEndpointUrl;
    
    @NotBlank(message = "Fetch endpoint URL is required")
    private String fetchEndpointUrl;
    
    @NotBlank(message = "Auth type is required")
    private IntegrationConfig.AuthType authType;
    
    private String username;
    
    private String password;
    
    private String apiKey;
    
    private Boolean enableDailySending = false;
    
    private String sendingTime = "09:00";
    
    private IntegrationConfig.Frequency frequency = IntegrationConfig.Frequency.DAILY;
    
    private Boolean isActive = true;
}