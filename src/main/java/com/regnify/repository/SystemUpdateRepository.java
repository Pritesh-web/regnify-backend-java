// src/main/java/com/regnify/repository/SystemUpdateRepository.java
package com.regnify.repository;

import com.regnify.model.SystemUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SystemUpdateRepository extends JpaRepository<SystemUpdate, Long> {
    
    List<SystemUpdate> findByIsActiveTrueOrderByUpdateDateDesc();
    
    List<SystemUpdate> findByTypeOrderByUpdateDateDesc(SystemUpdate.UpdateType type);
    
    @Query("SELECT su FROM SystemUpdate su WHERE su.updateDate >= :startDate AND su.updateDate <= :endDate ORDER BY su.updateDate DESC")
    List<SystemUpdate> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    List<SystemUpdate> findByVersionOrderByUpdateDateDesc(String version);
}