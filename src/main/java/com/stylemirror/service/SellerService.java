package com.stylemirror.service;

import com.stylemirror.dto.OrderDto;
import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.SellerDashboardDto;

import java.util.List;

public interface SellerService {
    SellerDashboardDto getDashboard(Long userId);
    List<ProductDto> getSellerProducts(Long userId);
    List<OrderDto> getSellerOrders(Long userId);
}
