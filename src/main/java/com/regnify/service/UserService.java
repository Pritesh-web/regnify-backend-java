// src/main/java/com/regnify/service/UserService.java
package com.regnify.service;

import com.regnify.dto.request.UserRequest;
import com.regnify.dto.response.UserResponse;
import com.regnify.model.User;
import com.regnify.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final EmailService emailService;
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapToUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapToUserResponse(user);
    }
    
    @Transactional
    public UserResponse createUser(UserRequest request, String createdBy) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Validate password
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        user.setCreatedBy(createdBy);
        user.setUpdatedBy(createdBy);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // Log the action
        auditService.logUserCreate(createdBy, savedUser.getId(), savedUser.getUsername());
        
        // Send welcome email
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName(), 
            savedUser.getUsername(), request.getPassword());
        
        return mapToUserResponse(savedUser);
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request, String updatedBy) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Check if new username exists (if changed)
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if new email exists (if changed)
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Store old values for audit
        String oldRole = user.getRole().name();
        String oldStatus = user.getStatus().name();
        
        // Update user
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());
        
        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (request.getPassword().length() < 8) {
                throw new RuntimeException("Password must be at least 8 characters long");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        
        // Log the update
        auditService.logUserUpdate(updatedBy, user.getId(), user.getUsername(), 
            oldRole, oldStatus, user.getRole().name(), user.getStatus().name());
        
        return mapToUserResponse(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id, String deletedBy) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        // Don't actually delete, just deactivate
        user.setStatus(User.Status.INACTIVE);
        user.setUpdatedBy(deletedBy);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        auditService.logUserDelete(deletedBy, user.getId(), user.getUsername());
    }
    
    @Transactional
    public void toggleUserStatus(Long id, String performedBy) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        User.Status newStatus = user.getStatus() == User.Status.ACTIVE ? 
            User.Status.INACTIVE : User.Status.ACTIVE;
        
        String oldStatus = user.getStatus().name();
        user.setStatus(newStatus);
        user.setUpdatedBy(performedBy);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        auditService.logUserStatusChange(performedBy, user.getId(), user.getUsername(), 
            oldStatus, newStatus.name());
        
        // Send notification email
        emailService.sendAccountStatusChangeEmail(user.getEmail(), user.getFirstName(), newStatus);
    }
    
    @Transactional
    public void changeUserRole(Long id, User.Role newRole, String performedBy) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        User.Role oldRole = user.getRole();
        user.setRole(newRole);
        user.setUpdatedBy(performedBy);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        auditService.logUserRoleChange(performedBy, user.getId(), user.getUsername(), 
            oldRole.name(), newRole.name());
        
        // Send notification email
        emailService.sendRoleChangeEmail(user.getEmail(), user.getFirstName(), newRole);
    }
    
    @Transactional
    public void unlockUserAccount(Long id, String performedBy) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        user.setAccountLocked(false);
        user.setAccountLockedUntil(null);
        user.setLoginAttempts(0);
        user.setUpdatedBy(performedBy);
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        
        auditService.logAccountUnlock(performedBy, user.getId(), user.getUsername());
        
        // Send notification email
        emailService.sendAccountUnlockedEmail(user.getEmail(), user.getFirstName());
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String query) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = userRepository.searchUsers(query, pageable);
        return users.getContent().stream()
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        Pageable pageable = PageRequest.of(0, 50);
        Page<User> users = userRepository.findByStatus(User.Status.ACTIVE, pageable);
        return users.getContent().stream()
            .map(this::mapToUserResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Long getUserCount() {
        return userRepository.count();
    }
    
    @Transactional(readOnly = true)
    public Long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }
    
    @Scheduled(fixedDelay = 300000) // Run every 5 minutes
    @Transactional
    public void unlockExpiredAccounts() {
        LocalDateTime now = LocalDateTime.now();
        List<User> usersToUnlock = userRepository.findUsersToUnlock(now);
        
        for (User user : usersToUnlock) {
            user.setAccountLocked(false);
            user.setAccountLockedUntil(null);
            user.setLoginAttempts(0);
            userRepository.save(user);
            
            log.info("Automatically unlocked account for user: {}", user.getUsername());
        }
    }
    
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getUsername(),
            user.getRole(),
            user.getStatus(),
            user.getLastLogin(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}