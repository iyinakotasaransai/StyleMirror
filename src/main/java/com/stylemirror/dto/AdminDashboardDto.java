package com.stylemirror.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDto {

    private long totalUsers;
    private long totalSellers;
    private long pendingSellers;
    private long totalProducts;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private List<OrderDto> recentOrders;
}
