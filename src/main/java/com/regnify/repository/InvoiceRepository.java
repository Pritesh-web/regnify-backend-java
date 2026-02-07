// src/main/java/com/regnify/repository/InvoiceRepository.java
package com.regnify.repository;

import com.regnify.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
    
    Page<Invoice> findByStatus(Invoice.Status status, Pageable pageable);
    
    Page<Invoice> findByCountry(Invoice.Country country, Pageable pageable);
    
    Page<Invoice> findByUploadedBy(String uploadedBy, Pageable pageable);
    
    Page<Invoice> findByStatusAndCountry(Invoice.Status status, Invoice.Country country, Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE " +
           "(:startDate IS NULL OR i.docDate >= :startDate) AND " +
           "(:endDate IS NULL OR i.docDate <= :endDate) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:country IS NULL OR i.country = :country) AND " +
           "(:documentType IS NULL OR i.documentType = :documentType) AND " +
           "(:sender IS NULL OR LOWER(i.sender) LIKE LOWER(CONCAT('%', :sender, '%'))) AND " +
           "(:receiver IS NULL OR LOWER(i.receiver) LIKE LOWER(CONCAT('%', :receiver, '%'))) AND " +
           "(:uploadedBy IS NULL OR i.uploadedBy = :uploadedBy) AND " +
           "i.deleted = false")
    Page<Invoice> findWithFilters(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("status") Invoice.Status status,
        @Param("country") Invoice.Country country,
        @Param("documentType") Invoice.DocumentType documentType,
        @Param("sender") String sender,
        @Param("receiver") String receiver,
        @Param("uploadedBy") String uploadedBy,
        Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE " +
           "(:search IS NULL OR " +
           "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.sender) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.receiver) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "i.deleted = false")
    Page<Invoice> searchInvoices(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.deleted = false")
    Long countTotalDocuments();
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'COMPLETE' AND i.deleted = false")
    Long countCompletedDocuments();
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'PENDING' AND i.deleted = false")
    Long countPendingDocuments();
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'ERROR' AND i.deleted = false")
    Long countErrorDocuments();
    
    @Query("SELECT i.country, COUNT(i) FROM Invoice i WHERE i.deleted = false GROUP BY i.country")
    List<Object[]> countByCountry();
    
    @Query("SELECT i.status, COUNT(i) FROM Invoice i WHERE i.deleted = false GROUP BY i.status")
    List<Object[]> countByStatus();
    
    @Query("SELECT i.documentType, COUNT(i) FROM Invoice i WHERE i.deleted = false GROUP BY i.documentType")
    List<Object[]> countByDocumentType();
    
    @Query("SELECT i.providerResponse, COUNT(i) FROM Invoice i WHERE i.deleted = false GROUP BY i.providerResponse")
    List<Object[]> countByProviderResponse();
    
    @Query("SELECT DATE(i.createdAt), COUNT(i), " +
           "SUM(CASE WHEN i.status = 'COMPLETE' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN i.status = 'ERROR' THEN 1 ELSE 0 END) " +
           "FROM Invoice i " +
           "WHERE i.createdAt >= :startDate AND i.deleted = false " +
           "GROUP BY DATE(i.createdAt) " +
           "ORDER BY DATE(i.createdAt)")
    List<Object[]> getDailyStats(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.status = 'PENDING' AND i.createdAt < :threshold")
    List<Invoice> findStalePendingInvoices(@Param("threshold") LocalDateTime threshold);
    
    @Query(value = "SELECT * FROM invoices WHERE " +
           "to_tsvector('english', invoice_number || ' ' || sender || ' ' || receiver) " +
           "@@ plainto_tsquery('english', :query) AND deleted = false",
           nativeQuery = true)
    Page<Invoice> fullTextSearch(@Param("query") String query, Pageable pageable);
}