// src/main/java/com/regnify/service/AuthService.java
package com.regnify.service;

import com.regnify.dto.request.LoginRequest;
import com.regnify.dto.response.LoginResponse;
import com.regnify.dto.response.UserResponse;
import com.regnify.model.User;
import com.regnify.repository.UserRepository;
import com.regnify.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        
        // Check if account is locked
        if (user.getAccountLocked() && user.getAccountLockedUntil() != null 
            && user.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            throw new LockedException("Account is locked. Try again after " + 
                user.getAccountLockedUntil());
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate tokens
            String token = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(username);
            
            // Update user login info
            user.setLastLogin(LocalDateTime.now());
            user.setLoginAttempts(0);
            user.setAccountLocked(false);
            user.setAccountLockedUntil(null);
            userRepository.save(user);
            
            // Create response
            UserResponse userResponse = mapToUserResponse(user);
            Long expiresIn = jwtTokenProvider.getTokenExpiration();
            
            // Log login
            auditService.logLogin(username, true, "Login successful");
            
            return new LoginResponse(token, refreshToken, expiresIn, userResponse);
            
        } catch (BadCredentialsException e) {
            // Increment login attempts
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            
            if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                log.warn("Account locked for user: {}", username);
            }
            
            userRepository.save(user);
            
            // Log failed login
            auditService.logLogin(username, false, "Invalid credentials");
            
            throw new BadCredentialsException("Invalid credentials");
        }
    }
    
    @Transactional
    public void logout(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        jwtTokenProvider.invalidateToken(token);
        auditService.logLogout(username);
    }
    
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("User not found"));
        
        if (!user.isEnabled()) {
            throw new LockedException("User account is disabled");
        }
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getUsername(), null, user.getAuthorities());
        
        String newToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
        UserResponse userResponse = mapToUserResponse(user);
        Long expiresIn = jwtTokenProvider.getTokenExpiration();
        
        return new LoginResponse(newToken, newRefreshToken, expiresIn, userResponse);
    }
    
    @Transactional
    public void resetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setAccountLocked(false);
        user.setLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);
        
        auditService.logPasswordReset(username);
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