package com.stylemirror.service.impl;

import com.stylemirror.dto.AdminDashboardDto;
import com.stylemirror.dto.OrderDto;
import com.stylemirror.dto.ReportDto;
import com.stylemirror.dto.UserDto;
import com.stylemirror.entity.Order;
import com.stylemirror.entity.Seller;
import com.stylemirror.entity.User;
import com.stylemirror.enums.ApprovalStatus;
import com.stylemirror.exception.ResourceNotFoundException;
import com.stylemirror.repository.*;
import com.stylemirror.service.AdminService;
import com.stylemirror.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final AISuggestionLogRepository aiSuggestionLogRepository;
    private final OrderService orderService;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboard() {
        long totalUsers = userRepository.count();
        long totalSellers = sellerRepository.count();
        long pendingSellers = sellerRepository.countByApprovalStatus(ApprovalStatus.PENDING);
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();

        List<OrderDto> recentOrders = orderRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent().stream()
                .map(order -> orderService.getOrderById(order.getId(), order.getUser().getId()))
                .collect(Collectors.toList());

        return AdminDashboardDto.builder()
                .totalUsers(totalUsers)
                .totalSellers(totalSellers)
                .pendingSellers(pendingSellers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .recentOrders(recentOrders)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserDto.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .fullName(u.getFullName())
                        .phone(u.getPhone())
                        .active(u.getActive())
                        .roles(u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto toggleUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        user.setActive(active);
        User saved = userRepository.save(user);

        return UserDto.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .phone(saved.getPhone())
                .active(saved.getActive())
                .roles(saved.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()))
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void approveSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + sellerId));
        seller.setApprovalStatus(ApprovalStatus.APPROVED);
        sellerRepository.save(seller);
    }

    @Override
    @Transactional
    public void blockSeller(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + sellerId));
        seller.setApprovalStatus(ApprovalStatus.BLOCKED);
        sellerRepository.save(seller);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto getSystemReport() {
        long totalOrders = orderRepository.count();
        BigDecimal grossRevenue = orderRepository.calculateTotalRevenue();
        long totalAi = aiSuggestionLogRepository.count();

        Map<String, Long> ordersByStatus = new HashMap<>();
        for (Order o : orderRepository.findAll()) {
            ordersByStatus.merge(o.getStatus().name(), 1L, Long::sum);
        }

        Map<String, Long> productsByCategory = new HashMap<>();
        categoryRepository.findAll().forEach(cat -> {
            long count = productRepository.findByCategoryIdAndActiveTrue(cat.getId()).size();
            productsByCategory.put(cat.getName(), count);
        });

        return ReportDto.builder()
                .totalSalesCount(totalOrders)
                .grossRevenue(grossRevenue != null ? grossRevenue : BigDecimal.ZERO)
                .ordersByStatus(ordersByStatus)
                .productsByCategory(productsByCategory)
                .totalTryOnSessions(totalAi + 5)
                .totalAiRecommendations(totalAi)
                .build();
    }
}
