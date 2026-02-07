// src/main/java/com/regnify/service/DashboardService.java
package com.regnify.service;

import com.regnify.dto.response.DashboardStatsResponse;
import com.regnify.repository.InvoiceRepository;
import com.regnify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        
        // Basic counts
        stats.setTotalDocuments(invoiceRepository.countTotalDocuments());
        stats.setTotalProcessed(invoiceRepository.countCompletedDocuments());
        stats.setTotalPending(invoiceRepository.countPendingDocuments());
        stats.setTotalErrors(invoiceRepository.countErrorDocuments());
        
        // Success rate calculation
        if (stats.getTotalDocuments() > 0) {
            double successRate = (stats.getTotalProcessed() * 100.0) / stats.getTotalDocuments();
            stats.setSuccessRate(Math.round(successRate * 100.0) / 100.0);
        } else {
            stats.setSuccessRate(0.0);
        }
        
        // Breakdown by country
        List<Object[]> countryCounts = invoiceRepository.countByCountry();
        Map<String, Long> countryMap = new HashMap<>();
        for (Object[] obj : countryCounts) {
            String country = obj[0].toString();
            Long count = (Long) obj[1];
            countryMap.put(country, count);
        }
        stats.setDocumentsByCountry(countryMap);
        
        // Breakdown by status
        List<Object[]> statusCounts = invoiceRepository.countByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] obj : statusCounts) {
            String status = obj[0].toString();
            Long count = (Long) obj[1];
            statusMap.put(status, count);
        }
        stats.setDocumentsByStatus(statusMap);
        
        // Breakdown by document type
        List<Object[]> docTypeCounts = invoiceRepository.countByDocumentType();
        Map<String, Long> docTypeMap = new HashMap<>();
        for (Object[] obj : docTypeCounts) {
            String docType = obj[0].toString();
            Long count = (Long) obj[1];
            docTypeMap.put(docType, count);
        }
        stats.setDocumentsByType(docTypeMap);
        
        // Provider response status
        List<Object[]> providerCounts = invoiceRepository.countByProviderResponse();
        Map<String, Long> providerMap = new HashMap<>();
        for (Object[] obj : providerCounts) {
            String response = obj[0].toString();
            Long count = (Long) obj[1];
            providerMap.put(response, count);
        }
        stats.setProviderResponseStatus(providerMap);
        
        // Daily stats for last 7 days
        LocalDateTime weekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        List<Object[]> dailyStats = invoiceRepository.getDailyStats(weekAgo);
        
        List<DashboardStatsResponse.DailyStats> dailyStatsList = new ArrayList<>();
        for (Object[] obj : dailyStats) {
            DashboardStatsResponse.DailyStats dailyStat = new DashboardStatsResponse.DailyStats();
            dailyStat.setDate(obj[0].toString());
            dailyStat.setCount((Long) obj[1]);
            dailyStat.setSuccessCount((Long) obj[2]);
            dailyStat.setErrorCount((Long) obj[3]);
            dailyStatsList.add(dailyStat);
        }
        stats.setDailyStats(dailyStatsList);
        
        // Country stats with percentages
        List<DashboardStatsResponse.CountryStats> countryStatsList = new ArrayList<>();
        for (Object[] obj : countryCounts) {
            String country = obj[0].toString();
            Long count = (Long) obj[1];
            double percentage = stats.getTotalDocuments() > 0 ? 
                (count * 100.0) / stats.getTotalDocuments() : 0;
            
            DashboardStatsResponse.CountryStats countryStat = 
                new DashboardStatsResponse.CountryStats(country, count, percentage);
            countryStatsList.add(countryStat);
        }
        stats.setCountryStats(countryStatsList);
        
        // Document type stats with percentages
        List<DashboardStatsResponse.DocumentTypeStats> docTypeStatsList = new ArrayList<>();
        for (Object[] obj : docTypeCounts) {
            String docType = obj[0].toString();
            Long count = (Long) obj[1];
            double percentage = stats.getTotalDocuments() > 0 ? 
                (count * 100.0) / stats.getTotalDocuments() : 0;
            
            DashboardStatsResponse.DocumentTypeStats docTypeStat = 
                new DashboardStatsResponse.DocumentTypeStats(docType, count, percentage);
            docTypeStatsList.add(docTypeStat);
        }
        stats.setDocumentTypeStats(docTypeStatsList);
        
        return stats;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getQuickStats() {
        Map<String, Object> quickStats = new HashMap<>();
        
        // Invoice stats
        quickStats.put("totalInvoices", invoiceRepository.countTotalDocuments());
        quickStats.put("pendingInvoices", invoiceRepository.countPendingDocuments());
        quickStats.put("processedToday", getProcessedTodayCount());
        quickStats.put("errorRate", getErrorRate());
        
        // User stats
        quickStats.put("totalUsers", userRepository.count());
        quickStats.put("activeUsers", userRepository.countActiveUsers());
        
        // System stats
        quickStats.put("uptime", getSystemUptime());
        quickStats.put("storageUsed", getStorageUsage());
        
        return quickStats;
    }
    
    private Long getProcessedTodayCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        // This would need a custom query - simplified for now
        return invoiceRepository.countCompletedDocuments();
    }
    
    private Double getErrorRate() {
        Long total = invoiceRepository.countTotalDocuments();
        Long errors = invoiceRepository.countErrorDocuments();
        
        if (total > 0) {
            return (errors * 100.0) / total;
        }
        return 0.0;
    }
    
    private String getSystemUptime() {
        // Simplified - in production would track actual start time
        return "99.9%";
    }
    
    private String getStorageUsage() {
        // Simplified - in production would calculate actual storage
        return "2.5 GB / 10 GB";
    }
}