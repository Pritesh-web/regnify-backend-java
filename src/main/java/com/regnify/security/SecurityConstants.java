// src/main/java/com/regnify/security/SecurityConstants.java
package com.regnify.security;

public class SecurityConstants {
    
    public static final String[] PUBLIC_URLS = {
        "/auth/**",
        "/public/**",
        "/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/webjars/**",
        "/configuration/ui",
        "/configuration/security"
    };
    
    public static final String[] USER_URLS = {
        "/invoices/**",
        "/dashboard/**",
        "/updates/**"
    };
    
    public static final String[] ADMIN_URLS = {
        "/users/**",
        "/integration/**",
        "/audit/**",
        "/system/**"
    };
    
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    
    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }
}