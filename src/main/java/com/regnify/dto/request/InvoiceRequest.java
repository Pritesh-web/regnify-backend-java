// src/main/java/com/regnify/dto/request/InvoiceRequest.java
package com.regnify.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.regnify.model.Invoice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {
    
    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;
    
    @NotNull(message = "Document date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate docDate;
    
    @NotNull(message = "Processing date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate proDate;
    
    @NotBlank(message = "Sender is required")
    private String sender;
    
    @NotBlank(message = "Receiver is required")
    private String receiver;
    
    private Invoice.Country country;
    
    private Invoice.DocumentType documentType;
    
    private MultipartFile file;
    
    private String invoiceName;
    
    private String notes;
}