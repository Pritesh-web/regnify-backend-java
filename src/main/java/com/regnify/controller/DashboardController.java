// src/main/java/com/regnify/controller/DashboardController.java
package com.regnify.controller;

import com.regnify.dto.response.ApiResponse;
import com.regnify.dto.response.DashboardStatsResponse;
import com.regnify.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics endpoints")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Get comprehensive dashboard statistics")
    @PreAuthorize("hasRole('VIEWER') or hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", stats));
    }
    
    @GetMapping("/quick-stats")
    @Operation(summary = "Get quick statistics", description = "Get quick overview statistics")
    @PreAuthorize("hasRole('VIEWER') or hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuickStats() {
        Map<String, Object> stats = dashboardService.getQuickStats();
        return ResponseEntity.ok(ApiResponse.success("Quick statistics retrieved successfully", stats));
    }
    
    @GetMapping("/health")
    @Operation(summary = "System health check", description = "Check system health status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis(),
            "service", "Invoice Validator",
            "version", "1.0.0"
        );
        return ResponseEntity.ok(ApiResponse.success("System is healthy", health));
    }
}