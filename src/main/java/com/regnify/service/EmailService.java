// src/main/java/com/regnify/service/EmailService.java
package com.regnify.service;

import com.regnify.model.Invoice;
import com.regnify.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Async
    public void sendWelcomeEmail(String to, String name, String username, String password) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("username", username);
            context.setVariable("password", password);
            context.setVariable("loginUrl", frontendUrl + "/login");
            
            String htmlContent = templateEngine.process("welcome-email", context);
            
            sendEmail(to, "Welcome to Regnify Invoice Validator", htmlContent);
            
            log.info("Welcome email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendInvoiceProcessedEmail(String to, Invoice invoice) {
        try {
            Context context = new Context();
            context.setVariable("invoiceNumber", invoice.getInvoiceNumber());
            context.setVariable("status", invoice.getStatus());
            context.setVariable("validationScore", invoice.getValidationScore());
            context.setVariable("invoiceUrl", frontendUrl + "/invoices/" + invoice.getId());
            
            String htmlContent = templateEngine.process("invoice-processed-email", context);
            
            sendEmail(to, "Invoice Processed: " + invoice.getInvoiceNumber(), htmlContent);
            
            log.info("Invoice processed email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send invoice processed email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendInvoiceValidationFailedEmail(String to, Invoice invoice, String validationErrors) {
        try {
            Context context = new Context();
            context.setVariable("invoiceNumber", invoice.getInvoiceNumber());
            context.setVariable("validationErrors", validationErrors);
            context.setVariable("invoiceUrl", frontendUrl + "/invoices/" + invoice.getId());
            
            String htmlContent = templateEngine.process("invoice-validation-failed-email", context);
            
            sendEmail(to, "Invoice Validation Failed: " + invoice.getInvoiceNumber(), htmlContent);
            
            log.info("Invoice validation failed email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send invoice validation failed email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendAccountStatusChangeEmail(String to, String name, User.Status status) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("status", status);
            context.setVariable("loginUrl", frontendUrl + "/login");
            
            String htmlContent = templateEngine.process("account-status-change-email", context);
            
            String subject = status == User.Status.ACTIVE ? 
                "Account Activated - Regnify" : "Account Deactivated - Regnify";
            
            sendEmail(to, subject, htmlContent);
            
            log.info("Account status change email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send account status change email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendRoleChangeEmail(String to, String name, User.Role role) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("role", role);
            context.setVariable("loginUrl", frontendUrl + "/login");
            
            String htmlContent = templateEngine.process("role-change-email", context);
            
            sendEmail(to, "Role Updated - Regnify", htmlContent);
            
            log.info("Role change email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send role change email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendAccountUnlockedEmail(String to, String name) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("loginUrl", frontendUrl + "/login");
            
            String htmlContent = templateEngine.process("account-unlocked-email", context);
            
            sendEmail(to, "Account Unlocked - Regnify", htmlContent);
            
            log.info("Account unlocked email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send account unlocked email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendPasswordResetEmail(String to, String name, String resetToken) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetUrl", frontendUrl + "/reset-password?token=" + resetToken);
            
            String htmlContent = templateEngine.process("password-reset-email", context);
            
            sendEmail(to, "Password Reset Request - Regnify", htmlContent);
            
            log.info("Password reset email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }
    
    @Async
    public void sendSystemAlertEmail(String to, String subject, String message) {
        try {
            Context context = new Context();
            context.setVariable("message", message);
            context.setVariable("timestamp", java.time.LocalDateTime.now().toString());
            
            String htmlContent = templateEngine.process("system-alert-email", context);
            
            sendEmail(to, "System Alert: " + subject, htmlContent);
            
            log.info("System alert email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send system alert email to {}: {}", to, e.getMessage());
        }
    }
    
    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
}