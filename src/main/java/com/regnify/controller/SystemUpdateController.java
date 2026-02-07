// src/main/java/com/regnify/controller/SystemUpdateController.java
package com.regnify.controller;

import com.regnify.dto.response.ApiResponse;
import com.regnify.model.SystemUpdate;
import com.regnify.service.SystemUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
@Tag(name = "System Updates", description = "System update management endpoints")
public class SystemUpdateController {
    
    private final SystemUpdateService systemUpdateService;
    
    @GetMapping
    @Operation(summary = "Get all updates", description = "Get all system updates")
    @PreAuthorize("hasRole('VIEWER') or hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<List<SystemUpdate>>> getAllUpdates() {
        List<SystemUpdate> updates = systemUpdateService.getAllUpdates();
        return ResponseEntity.ok(ApiResponse.success("System updates retrieved successfully", updates));
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Get updates by type", description = "Get system updates by type")
    @PreAuthorize("hasRole('VIEWER') or hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<List<SystemUpdate>>> getUpdatesByType(@PathVariable SystemUpdate.UpdateType type) {
        List<SystemUpdate> updates = systemUpdateService.getUpdatesByType(type);
        return ResponseEntity.ok(ApiResponse.success("System updates retrieved successfully", updates));
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get updates by date range", description = "Get system updates by date range")
    @PreAuthorize("hasRole('VIEWER') or hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<List<SystemUpdate>>> getUpdatesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        
        List<SystemUpdate> updates = systemUpdateService.getUpdatesByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("System updates retrieved successfully", updates));
    }
    
    @GetMapping("/version/{version}")
    @Operation(summary = "Get updates by version", description = "Get system updates by version")
    @PreAuthorize("hasRole('VIEWER') or hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<List<SystemUpdate>>> getUpdatesByVersion(@PathVariable String version) {
        List<SystemUpdate> updates = systemUpdateService.getUpdatesByVersion(version);
        return ResponseEntity.ok(ApiResponse.success("System updates retrieved successfully", updates));
    }
    
    @PostMapping
    @Operation(summary = "Create update", description = "Create a new system update")
    @PreAuthorize("hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<SystemUpdate>> createUpdate(@RequestBody SystemUpdate update) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdBy = authentication.getName();
        
        SystemUpdate createdUpdate = systemUpdateService.createUpdate(update, createdBy);
        return ResponseEntity.ok(ApiResponse.success("System update created successfully", createdUpdate));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update update", description = "Update an existing system update")
    @PreAuthorize("hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<SystemUpdate>> updateUpdate(
            @PathVariable Long id,
            @RequestBody SystemUpdate update) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String updatedBy = authentication.getName();
        
        SystemUpdate updated = systemUpdateService.updateUpdate(id, update, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("System update updated successfully", updated));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete update", description = "Delete a system update")
    @PreAuthorize("hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteUpdate(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String deletedBy = authentication.getName();
        
        systemUpdateService.deleteUpdate(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("System update deleted successfully", null));
    }
}