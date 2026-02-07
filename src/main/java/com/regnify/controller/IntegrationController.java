// src/main/java/com/regnify/controller/IntegrationController.java
package com.regnify.controller;

import com.regnify.dto.request.IntegrationConfigRequest;
import com.regnify.dto.response.ApiResponse;
import com.regnify.dto.response.IntegrationConfigResponse;
import com.regnify.service.IntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/integration")
@RequiredArgsConstructor
@Tag(name = "Integration", description = "Integration configuration endpoints")
public class IntegrationController {
    
    private final IntegrationService integrationService;
    
    @GetMapping("/configs")
    @Operation(summary = "Get all integration configs", description = "Get all integration configurations")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<List<IntegrationConfigResponse>>> getAllConfigs() {
        List<IntegrationConfigResponse> configs = integrationService.getAllConfigs();
        return ResponseEntity.ok(ApiResponse.success("Integration configs retrieved successfully", configs));
    }
    
    @GetMapping("/configs/{id}")
    @Operation(summary = "Get integration config by ID", description = "Get integration configuration by ID")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<IntegrationConfigResponse>> getConfigById(@PathVariable Long id) {
        IntegrationConfigResponse config = integrationService.getConfigById(id);
        return ResponseEntity.ok(ApiResponse.success("Integration config retrieved successfully", config));
    }
    
    @GetMapping("/configs/provider/{providerName}")
    @Operation(summary = "Get integration config by provider name", description = "Get integration configuration by provider name")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<IntegrationConfigResponse>> getConfigByProviderName(
            @PathVariable String providerName) {
        IntegrationConfigResponse config = integrationService.getConfigByProviderName(providerName);
        return ResponseEntity.ok(ApiResponse.success("Integration config retrieved successfully", config));
    }
    
    @PostMapping("/configs")
    @Operation(summary = "Create integration config", description = "Create a new integration configuration")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<IntegrationConfigResponse>> createConfig(
            @Valid @RequestBody IntegrationConfigRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdBy = authentication.getName();
        
        IntegrationConfigResponse config = integrationService.createConfig(request, createdBy);
        return ResponseEntity.ok(ApiResponse.success("Integration config created successfully", config));
    }
    
    @PutMapping("/configs/{id}")
    @Operation(summary = "Update integration config", description = "Update an existing integration configuration")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<IntegrationConfigResponse>> updateConfig(
            @PathVariable Long id,
            @Valid @RequestBody IntegrationConfigRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String updatedBy = authentication.getName();
        
        IntegrationConfigResponse config = integrationService.updateConfig(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Integration config updated successfully", config));
    }
    
    @DeleteMapping("/configs/{id}")
    @Operation(summary = "Delete integration config", description = "Delete an integration configuration")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteConfig(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String deletedBy = authentication.getName();
        
        integrationService.deleteConfig(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("Integration config deleted successfully", null));
    }
    
    @PatchMapping("/configs/{id}/toggle-status")
    @Operation(summary = "Toggle config status", description = "Toggle integration configuration active status")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<IntegrationConfigResponse>> toggleConfigStatus(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = authentication.getName();
        
        IntegrationConfigResponse config = integrationService.toggleConfigStatus(id, performedBy);
        return ResponseEntity.ok(ApiResponse.success("Integration config status updated successfully", config));
    }
    
    @PostMapping("/configs/{id}/generate-credentials")
    @Operation(summary = "Generate credentials", description = "Generate client credentials for integration")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<IntegrationConfigResponse>> generateCredentials(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = authentication.getName();
        
        IntegrationConfigResponse config = integrationService.generateCredentials(id, performedBy);
        return ResponseEntity.ok(ApiResponse.success("Credentials generated successfully", config));
    }
    
    @PostMapping("/configs/{id}/test-connection")
    @Operation(summary = "Test connection", description = "Test integration connection")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<String>> testConnection(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = authentication.getName();
        
        String result = integrationService.testConnection(id, performedBy);
        return ResponseEntity.ok(ApiResponse.success(result, null));
    }
    
    @PostMapping("/configs/{id}/fetch-status")
    @Operation(summary = "Fetch status", description = "Fetch status from integration endpoint")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<String>> fetchStatus(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = authentication.getName();
        
        String result = integrationService.fetchStatus(id, performedBy);
        return ResponseEntity.ok(ApiResponse.success(result, null));
    }
}