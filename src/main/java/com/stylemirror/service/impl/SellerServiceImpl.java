package com.stylemirror.service.impl;

import com.stylemirror.dto.OrderDto;
import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.ProductVariantDto;
import com.stylemirror.dto.SellerDashboardDto;
import com.stylemirror.entity.Seller;
import com.stylemirror.exception.ForbiddenException;
import com.stylemirror.repository.OrderRepository;
import com.stylemirror.repository.ProductRepository;
import com.stylemirror.repository.ProductVariantRepository;
import com.stylemirror.repository.SellerRepository;
import com.stylemirror.service.OrderService;
import com.stylemirror.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository;
    private final ProductServiceImpl productService;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public SellerDashboardDto getDashboard(Long userId) {
        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ForbiddenException("You are not registered as a seller"));

        long totalProducts = productRepository.countBySellerId(seller.getId());
        long activeProducts = productRepository.countBySellerIdAndActiveTrue(seller.getId());
        long pendingOrders = orderRepository.countPendingOrdersBySellerId(seller.getId());
        BigDecimal totalSales = orderRepository.calculateTotalSalesBySellerId(seller.getId());
        long totalOrders = orderRepository.findOrdersBySellerId(seller.getId()).size();

        List<ProductVariantDto> lowStock = productVariantRepository.findLowStockVariantsBySeller(seller.getId(), 15).stream()
                .map(v -> ProductVariantDto.builder()
                        .id(v.getId())
                        .productId(v.getProduct().getId())
                        .size(v.getSize())
                        .color(v.getColor())
                        .price(v.getPrice())
                        .stock(v.getStock())
                        .sku(v.getSku())
                        .active(v.getActive())
                        .build())
                .collect(Collectors.toList());

        return SellerDashboardDto.builder()
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .lowStockProducts(lowStock)
                .storeName(seller.getStoreName())
                .approvalStatus(seller.getApprovalStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getSellerProducts(Long userId) {
        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ForbiddenException("You are not registered as a seller"));

        return productRepository.findBySellerId(seller.getId()).stream()
                .map(productService::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getSellerOrders(Long userId) {
        return orderService.getSellerOrders(userId);
    }
}
