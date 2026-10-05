package com.stylemirror.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {

    private long totalSalesCount;
    private BigDecimal grossRevenue;
    private Map<String, Long> ordersByStatus;
    private Map<String, Long> productsByCategory;
    private long totalTryOnSessions;
    private long totalAiRecommendations;
}
