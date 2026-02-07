// src/main/java/com/regnify/dto/response/DashboardStatsResponse.java
package com.regnify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    
    private Long totalDocuments;
    private Long totalProcessed;
    private Long totalPending;
    private Long totalErrors;
    private Double successRate;
    
    private Map<String, Long> documentsByCountry;
    private Map<String, Long> documentsByStatus;
    private Map<String, Long> documentsByType;
    private Map<String, Long> providerResponseStatus;
    
    private List<DailyStats> dailyStats;
    private List<CountryStats> countryStats;
    private List<DocumentTypeStats> documentTypeStats;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private String date;
        private Long count;
        private Long successCount;
        private Long errorCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryStats {
        private String country;
        private Long count;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentTypeStats {
        private String documentType;
        private Long count;
        private Double percentage;
    }
}