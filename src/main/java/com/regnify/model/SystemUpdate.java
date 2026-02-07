// src/main/java/com/regnify/model/SystemUpdate.java
package com.regnify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUpdate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "update_date", nullable = false)
    private LocalDate updateDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UpdateType type;
    
    @Column(name = "version", length = 20)
    private String version;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    public enum UpdateType {
        FEATURE,
        BUG_FIX,
        ENHANCEMENT,
        SECURITY,
        MAINTENANCE
    }
}