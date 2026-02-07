// src/main/java/com/regnify/model/Invoice.java
package com.regnify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_invoices_invoice_number", columnList = "invoice_number", unique = true),
    @Index(name = "idx_invoices_status", columnList = "status"),
    @Index(name = "idx_invoices_country", columnList = "country"),
    @Index(name = "idx_invoices_doc_date", columnList = "doc_date"),
    @Index(name = "idx_invoices_sender", columnList = "sender"),
    @Index(name = "idx_invoices_receiver", columnList = "receiver")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "invoice_number", nullable = false, unique = true, length = 100)
    private String invoiceNumber;
    
    @Column(name = "doc_date", nullable = false)
    private LocalDate docDate;
    
    @Column(name = "pro_date", nullable = false)
    private LocalDate proDate;
    
    @Column(nullable = false, length = 255)
    private String sender;
    
    @Column(nullable = false, length = 255)
    private String receiver;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "business_status", nullable = false, length = 20)
    private BusinessStatus businessStatus = BusinessStatus.PENDING_REVIEW;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_response", nullable = false, length = 20)
    private ProviderResponse providerResponse = ProviderResponse.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Country country;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType = DocumentType.INVOICE;
    
    @Column(name = "file_name", length = 255)
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_content_type", length = 100)
    private String fileContentType;
    
    @Column(name = "file_path", length = 500)
    private String filePath;
    
    @Column(name = "validation_errors", columnDefinition = "TEXT")
    private String validationErrors;
    
    @Column(name = "validation_score")
    private Integer validationScore = 0;
    
    @Column(name = "uploaded_by", nullable = false, length = 100)
    private String uploadedBy;
    
    @Column(name = "processed_by", length = 100)
    private String processedBy;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "deleted_by", length = 100)
    private String deletedBy;
    
    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETE,
        ERROR,
        REJECTED
    }
    
    public enum BusinessStatus {
        PENDING_REVIEW,
        APPROVED,
        REJECTED,
        UNDER_REVIEW,
        ESCALATED
    }
    
    public enum ProviderResponse {
        PENDING,
        SUCCESS,
        FAILED,
        TIMEOUT,
        RETRY
    }
    
    public enum Country {
        GERMANY,
        FRANCE,
        UK,
        SPAIN,
        ITALY,
        NETHERLANDS,
        BELGIUM,
        SWITZERLAND,
        AUSTRIA,
        OTHER
    }
    
    public enum DocumentType {
        INVOICE,
        CREDIT_NOTE,
        DEBIT_NOTE,
        RECEIPT,
        STATEMENT,
        ORDER,
        DELIVERY_NOTE
    }
}