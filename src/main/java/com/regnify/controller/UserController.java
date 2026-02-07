// src/main/java/com/regnify/controller/UserController.java
package com.regnify.controller;

import com.regnify.dto.request.UserRequest;
import com.regnify.dto.response.ApiResponse;
import com.regnify.dto.response.UserResponse;
import com.regnify.model.User;
import com.regnify.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Get paginated list of all users")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Page<UserResponse> users = userService.getAllUsers(page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get detailed information about a specific user")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Get user by username")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String createdBy = authentication.getName();
        
        UserResponse user = userService.createUser(request, createdBy);
        return ResponseEntity.ok(ApiResponse.success("User created successfully", user));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String updatedBy = authentication.getName();
        
        UserResponse user = userService.updateUser(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deactivate a user")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String deletedBy = authentication.getName();
        
        userService.deleteUser(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
    
    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle user status", description = "Toggle user active/inactive status")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = authentication.getName();
        
        userService.toggleUserStatus(id, performedBy);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", null));
    }
    
    @PatchMapping("/{id}/change-role")
    @Operation(summary = "Change user role", description = "Change user role")
    @PreAuthorize("hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> changeUserRole(
            @PathVariable Long id,
            @Parameter(description = "New role") @RequestParam User.Role role) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = authentication.getName();
        
        userService.changeUserRole(id, role, performedBy);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", null));
    }
    
    @PostMapping("/{id}/unlock")
    @Operation(summary = "Unlock user account", description = "Unlock a locked user account")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> unlockUserAccount(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String performedBy = authentication.getName();
        
        userService.unlockUserAccount(id, performedBy);
        return ResponseEntity.ok(ApiResponse.success("User account unlocked successfully", null));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name, email, or username")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam String query) {
        List<UserResponse> users = userService.searchUsers(query);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active users", description = "Get list of active users")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getActiveUsers() {
        List<UserResponse> users = userService.getActiveUsers();
        return ResponseEntity.ok(ApiResponse.success("Active users retrieved successfully", users));
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get user count", description = "Get total user count")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Long>> getUserCount() {
        Long count = userService.getUserCount();
        return ResponseEntity.ok(ApiResponse.success("User count retrieved successfully", count));
    }
    
    @GetMapping("/active-count")
    @Operation(summary = "Get active user count", description = "Get active user count")
    @PreAuthorize("hasRole('SUPER_USER') or hasRole('ADMIN_MODERATOR')")
    public ResponseEntity<ApiResponse<Long>> getActiveUserCount() {
        Long count = userService.getActiveUserCount();
        return ResponseEntity.ok(ApiResponse.success("Active user count retrieved successfully", count));
    }
}