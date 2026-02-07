// src/main/java/com/regnify/model/IntegrationConfig.java
package com.regnify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "integration_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IntegrationConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "service_provider_name", nullable = false, length = 255)
    private String serviceProviderName;
    
    @Column(name = "send_endpoint_url", nullable = false, length = 500)
    private String sendEndpointUrl;
    
    @Column(name = "fetch_endpoint_url", nullable = false, length = 500)
    private String fetchEndpointUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, length = 20)
    private AuthType authType;
    
    @Column(name = "username", length = 100)
    private String username;
    
    @Column(name = "password", length = 255)
    private String password;
    
    @Column(name = "api_key", length = 500)
    private String apiKey;
    
    @Column(name = "client_key", length = 255)
    private String clientKey;
    
    @Column(name = "client_secret", length = 500)
    private String clientSecret;
    
    @Column(name = "access_token", length = 1000)
    private String accessToken;
    
    @Column(name = "refresh_token", length = 1000)
    private String refreshToken;
    
    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;
    
    @Column(name = "enable_daily_sending")
    private Boolean enableDailySending = false;
    
    @Column(name = "sending_time", length = 10)
    private String sendingTime = "09:00";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", length = 20)
    private Frequency frequency = Frequency.DAILY;
    
    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;
    
    @Column(name = "sync_status", length = 20)
    private String syncStatus;
    
    @Column(name = "sync_errors", columnDefinition = "TEXT")
    private String syncErrors;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    public enum AuthType {
        BASIC,
        API_KEY,
        OAUTH2,
        BEARER_TOKEN,
        CUSTOM
    }
    
    public enum Frequency {
        DAILY,
        WEEKLY,
        MONTHLY,
        HOURLY,
        MANUAL
    }
}