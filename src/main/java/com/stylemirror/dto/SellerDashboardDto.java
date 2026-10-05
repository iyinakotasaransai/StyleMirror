package com.stylemirror.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDashboardDto {

    private long totalProducts;
    private long activeProducts;
    private long totalOrders;
    private long pendingOrders;
    private BigDecimal totalSales;
    private List<ProductVariantDto> lowStockProducts;
    private String storeName;
    private String approvalStatus;
}
