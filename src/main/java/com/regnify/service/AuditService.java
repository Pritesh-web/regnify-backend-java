// src/main/java/com/regnify/service/AuditService.java
package com.regnify.service;

import com.regnify.model.AuditLog;
import com.regnify.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;
    
    @Transactional
    public void logLogin(String username, boolean success, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(success ? "LOGIN_SUCCESS" : "LOGIN_FAILED");
        auditLog.setEntityType("USER");
        auditLog.setPerformedBy(username);
        auditLog.setStatus(success ? "SUCCESS" : "FAILED");
        auditLog.setErrorMessage(success ? null : details);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Login {} for user: {}", success ? "successful" : "failed", username);
    }
    
    @Transactional
    public void logLogout(String username) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("LOGOUT");
        auditLog.setEntityType("USER");
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Logout for user: {}", username);
    }
    
    @Transactional
    public void logInvoiceUpload(String username, Long invoiceId, String invoiceNumber, String status) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("INVOICE_UPLOAD");
        auditLog.setEntityType("INVOICE");
        auditLog.setEntityId(invoiceId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus(status);
        auditLog.setNewValue("Invoice " + invoiceNumber + " uploaded");
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Invoice uploaded by {}: {}", username, invoiceNumber);
    }
    
    @Transactional
    public void logInvoiceUpdate(String username, Long invoiceId, String invoiceNumber) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("INVOICE_UPDATE");
        auditLog.setEntityType("INVOICE");
        auditLog.setEntityId(invoiceId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Invoice " + invoiceNumber + " updated");
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Invoice updated by {}: {}", username, invoiceNumber);
    }
    
    @Transactional
    public void logInvoiceDelete(String username, Long invoiceId, String invoiceNumber) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("INVOICE_DELETE");
        auditLog.setEntityType("INVOICE");
        auditLog.setEntityId(invoiceId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Invoice " + invoiceNumber + " deleted");
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Invoice deleted by {}: {}", username, invoiceNumber);
    }
    
    @Transactional
    public void logInvoiceProcess(String username, Long invoiceId, String invoiceNumber) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("INVOICE_PROCESS");
        auditLog.setEntityType("INVOICE");
        auditLog.setEntityId(invoiceId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Invoice " + invoiceNumber + " processed");
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Invoice processed by {}: {}", username, invoiceNumber);
    }
    
    @Transactional
    public void logUserCreate(String username, Long userId, String targetUsername) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("USER_CREATE");
        auditLog.setEntityType("USER");
        auditLog.setEntityId(userId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("User " + targetUsername + " created");
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("User created by {}: {}", username, targetUsername);
    }
    
    @Transactional
    public void logUserUpdate(String username, Long userId, String targetUsername, 
                             String oldRole, String oldStatus, String newRole, String newStatus) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("USER_UPDATE");
        auditLog.setEntityType("USER");
        auditLog.setEntityId(userId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setOldValue("Role: " + oldRole + ", Status: " + oldStatus);
        auditLog.setNewValue("Role: " + newRole + ", Status: " + newStatus);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("User updated by {}: {}", username, targetUsername);
    }
    
    @Transactional
    public void logUserDelete(String username, Long userId, String targetUsername) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("USER_DELETE");
        auditLog.setEntityType("USER");
        auditLog.setEntityId(userId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("User " + targetUsername + " deleted");
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("User deleted by {}: {}", username, targetUsername);
    }
    
    @Transactional
    public void logUserStatusChange(String username, Long userId, String targetUsername, 
                                   String oldStatus, String newStatus) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("USER_STATUS_CHANGE");
        auditLog.setEntityType("USER");
        auditLog.setEntityId(userId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setOldValue("Status: " + oldStatus);
        auditLog.setNewValue("Status: " + newStatus);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("User status changed by {} for {}: {} -> {}", 
            username, targetUsername, oldStatus, newStatus);
    }
    
    @Transactional
    public void logUserRoleChange(String username, Long userId, String targetUsername, 
                                 String oldRole, String newRole) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("USER_ROLE_CHANGE");
        auditLog.setEntityType("USER");
        auditLog.setEntityId(userId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setOldValue("Role: " + oldRole);
        auditLog.setNewValue("Role: " + newRole);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("User role changed by {} for {}: {} -> {}", 
            username, targetUsername, oldRole, newRole);
    }
    
    @Transactional
    public void logAccountUnlock(String username, Long userId, String targetUsername) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("ACCOUNT_UNLOCK");
        auditLog.setEntityType("USER");
        auditLog.setEntityId(userId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Account unlocked for " + targetUsername);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Account unlocked by {} for {}", username, targetUsername);
    }
    
    @Transactional
    public void logPasswordReset(String username) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("PASSWORD_RESET");
        auditLog.setEntityType("USER");
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Password reset for " + username);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Password reset for user: {}", username);
    }
    
    @Transactional
    public void logIntegrationConfigCreate(String username, Long configId, String providerName) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("INTEGRATION_CONFIG_CREATE");
        auditLog.setEntityType("INTEGRATION_CONFIG");
        auditLog.setEntityId(configId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Integration config created for " + providerName);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Integration config created by {}: {}", username, providerName);
    }
    
    @Transactional
    public void logIntegrationConfigUpdate(String username, Long configId, String providerName) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("INTEGRATION_CONFIG_UPDATE");
        auditLog.setEntityType("INTEGRATION_CONFIG");
        auditLog.setEntityId(configId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Integration config updated for " + providerName);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Integration config updated by {}: {}", username, providerName);
    }
    
    @Transactional
    public void logIntegrationConfigDelete(String username, Long configId, String providerName) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("INTEGRATION_CONFIG_DELETE");
        auditLog.setEntityType("INTEGRATION_CONFIG");
        auditLog.setEntityId(configId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Integration config deleted for " + providerName);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Integration config deleted by {}: {}", username, providerName);
    }
    
    @Transactional
    public void logIntegrationConfigStatusChange(String username, Long configId, 
                                               String providerName, boolean newStatus) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("INTEGRATION_CONFIG_STATUS_CHANGE");
        auditLog.setEntityType("INTEGRATION_CONFIG");
        auditLog.setEntityId(configId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setOldValue("Status: " + !newStatus);
        auditLog.setNewValue("Status: " + newStatus);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Integration config status changed by {} for {}: {}", 
            username, providerName, newStatus);
    }
    
    @Transactional
    public void logCredentialsGenerate(String username, Long configId, String providerName) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("CREDENTIALS_GENERATE");
        auditLog.setEntityType("INTEGRATION_CONFIG");
        auditLog.setEntityId(configId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("Credentials generated for " + providerName);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Credentials generated by {} for {}", username, providerName);
    }
    
    @Transactional
    public void logConnectionTest(String username, Long configId, String providerName, 
                                 boolean success, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("CONNECTION_TEST");
        auditLog.setEntityType("INTEGRATION_CONFIG");
        auditLog.setEntityId(configId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus(success ? "SUCCESS" : "FAILED");
        auditLog.setNewValue("Connection test " + (success ? "passed" : "failed") + " for " + providerName);
        auditLog.setErrorMessage(success ? null : details);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Connection test {} by {} for {}", 
            success ? "passed" : "failed", username, providerName);
    }
    
    @Transactional
    public void logStatusFetch(String username, Long configId, String providerName, 
                              boolean success, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("STATUS_FETCH");
        auditLog.setEntityType("INTEGRATION_CONFIG");
        auditLog.setEntityId(configId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus(success ? "SUCCESS" : "FAILED");
        auditLog.setNewValue("Status fetch " + (success ? "successful" : "failed") + " for " + providerName);
        auditLog.setErrorMessage(success ? null : details);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("Status fetch {} by {} for {}", 
            success ? "successful" : "failed", username, providerName);
    }
    
    @Transactional
    public void logSystemUpdateCreate(String username, Long updateId, String title) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("SYSTEM_UPDATE_CREATE");
        auditLog.setEntityType("SYSTEM_UPDATE");
        auditLog.setEntityId(updateId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("System update created: " + title);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("System update created by {}: {}", username, title);
    }
    
    @Transactional
    public void logSystemUpdateUpdate(String username, Long updateId, String title) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("SYSTEM_UPDATE_UPDATE");
        auditLog.setEntityType("SYSTEM_UPDATE");
        auditLog.setEntityId(updateId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("System update updated: " + title);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("System update updated by {}: {}", username, title);
    }
    
    @Transactional
    public void logSystemUpdateDelete(String username, Long updateId, String title) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("SYSTEM_UPDATE_DELETE");
        auditLog.setEntityType("SYSTEM_UPDATE");
        auditLog.setEntityId(updateId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("SUCCESS");
        auditLog.setNewValue("System update deleted: " + title);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.info("System update deleted by {}: {}", username, title);
    }
    
    @Transactional
    public void logError(String username, String action, String entityType, 
                        Long entityId, String errorMessage) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setPerformedBy(username);
        auditLog.setStatus("ERROR");
        auditLog.setErrorMessage(errorMessage);
        auditLog.setIpAddress(getClientIp());
        auditLog.setUserAgent(getUserAgent());
        
        auditLogRepository.save(auditLog);
        log.error("Error logged for user {}: {}", username, errorMessage);
    }
    
    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
    
    private String getUserAgent() {
        return request.getHeader("User-Agent");
    }
}