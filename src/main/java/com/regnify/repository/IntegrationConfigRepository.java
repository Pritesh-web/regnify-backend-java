// src/main/java/com/regnify/repository/IntegrationConfigRepository.java
package com.regnify.repository;

import com.regnify.model.IntegrationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntegrationConfigRepository extends JpaRepository<IntegrationConfig, Long> {
    
    Optional<IntegrationConfig> findByServiceProviderName(String serviceProviderName);
    
    List<IntegrationConfig> findByIsActiveTrue();
    
    @Query("SELECT ic FROM IntegrationConfig ic WHERE ic.enableDailySending = true AND ic.isActive = true")
    List<IntegrationConfig> findActiveScheduledConfigs();
    
    boolean existsByServiceProviderName(String serviceProviderName);
}