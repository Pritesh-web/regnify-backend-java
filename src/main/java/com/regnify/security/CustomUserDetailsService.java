// src/main/java/com/regnify/security/CustomUserDetailsService.java
package com.regnify.security;

import com.regnify.model.User;
import com.regnify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User account is disabled");
        }
        
        if (!user.isAccountNonLocked()) {
            throw new UsernameNotFoundException("User account is locked");
        }
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getAuthorities())
            .accountExpired(false)
            .accountLocked(!user.isAccountNonLocked())
            .credentialsExpired(false)
            .disabled(!user.isEnabled())
            .build();
    }
}