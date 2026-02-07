// src/main/java/com/regnify/repository/AuditLogRepository.java
package com.regnify.repository;

import com.regnify.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    Page<AuditLog> findByPerformedBy(String performedBy, Pageable pageable);
    
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    
    Page<AuditLog> findByAction(String action, Pageable pageable);
    
    @Query("SELECT al FROM AuditLog al WHERE " +
           "(:startDate IS NULL OR al.performedAt >= :startDate) AND " +
           "(:endDate IS NULL OR al.performedAt <= :endDate) AND " +
           "(:entityType IS NULL OR al.entityType = :entityType) AND " +
           "(:action IS NULL OR al.action = :action) AND " +
           "(:performedBy IS NULL OR al.performedBy = :performedBy)")
    Page<AuditLog> findWithFilters(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("entityType") String entityType,
        @Param("action") String action,
        @Param("performedBy") String performedBy,
        Pageable pageable);
}