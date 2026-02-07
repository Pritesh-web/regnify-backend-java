// src/main/java/com/regnify/dto/response/InvoiceResponse.java
package com.regnify.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.regnify.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    
    private Long id;
    private String invoiceNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate docDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate proDate;
    
    private String sender;
    private String receiver;
    private Invoice.Status status;
    private Invoice.BusinessStatus businessStatus;
    private Invoice.ProviderResponse providerResponse;
    private Invoice.Country country;
    private Invoice.DocumentType documentType;
    private String fileName;
    private Long fileSize;
    private String uploadedBy;
    private String processedBy;
    private String validationErrors;
    private Integer validationScore;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}