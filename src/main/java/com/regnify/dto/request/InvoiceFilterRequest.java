// src/main/java/com/regnify/dto/request/InvoiceFilterRequest.java
package com.regnify.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.regnify.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceFilterRequest {
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String invoiceNumber;
    
    private Invoice.Status status;
    
    private Invoice.Country country;
    
    private Invoice.DocumentType documentType;
    
    private String sender;
    
    private String receiver;
    
    private String uploadedBy;
    
    private Integer page = 0;
    
    private Integer size = 10;
    
    private String sortBy = "createdAt";
    
    private String sortDirection = "DESC";
}