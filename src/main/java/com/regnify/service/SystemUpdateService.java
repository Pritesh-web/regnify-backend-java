// src/main/java/com/regnify/service/SystemUpdateService.java
package com.regnify.service;

import com.regnify.model.SystemUpdate;
import com.regnify.repository.SystemUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemUpdateService {
    
    private final SystemUpdateRepository systemUpdateRepository;
    private final AuditService auditService;
    
    @Transactional(readOnly = true)
    public List<SystemUpdate> getAllUpdates() {
        return systemUpdateRepository.findByIsActiveTrueOrderByUpdateDateDesc();
    }
    
    @Transactional(readOnly = true)
    public List<SystemUpdate> getUpdatesByType(SystemUpdate.UpdateType type) {
        return systemUpdateRepository.findByTypeOrderByUpdateDateDesc(type);
    }
    
    @Transactional(readOnly = true)
    public List<SystemUpdate> getUpdatesByDateRange(LocalDate startDate, LocalDate endDate) {
        return systemUpdateRepository.findByDateRange(startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<SystemUpdate> getUpdatesByVersion(String version) {
        return systemUpdateRepository.findByVersionOrderByUpdateDateDesc(version);
    }
    
    @Transactional
    public SystemUpdate createUpdate(SystemUpdate update, String createdBy) {
        update.setCreatedBy(createdBy);
        update.setCreatedAt(java.time.LocalDateTime.now());
        update.setIsActive(true);
        
        SystemUpdate savedUpdate = systemUpdateRepository.save(update);
        
        auditService.logSystemUpdateCreate(createdBy, savedUpdate.getId(), savedUpdate.getTitle());
        
        return savedUpdate;
    }
    
    @Transactional
    public SystemUpdate updateUpdate(Long id, SystemUpdate update, String updatedBy) {
        SystemUpdate existingUpdate = systemUpdateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("System update not found"));
        
        existingUpdate.setTitle(update.getTitle());
        existingUpdate.setDescription(update.getDescription());
        existingUpdate.setUpdateDate(update.getUpdateDate());
        existingUpdate.setType(update.getType());
        existingUpdate.setVersion(update.getVersion());
        
        SystemUpdate updated = systemUpdateRepository.save(existingUpdate);
        
        auditService.logSystemUpdateUpdate(updatedBy, updated.getId(), updated.getTitle());
        
        return updated;
    }
    
    @Transactional
    public void deleteUpdate(Long id, String deletedBy) {
        SystemUpdate update = systemUpdateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("System update not found"));
        
        update.setIsActive(false);
        systemUpdateRepository.save(update);
        
        auditService.logSystemUpdateDelete(deletedBy, update.getId(), update.getTitle());
    }
}