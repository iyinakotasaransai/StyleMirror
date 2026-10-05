package com.stylemirror.service;

import com.stylemirror.dto.OrderCreateRequest;
import com.stylemirror.dto.OrderDto;
import com.stylemirror.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(Long userId, OrderCreateRequest request);
    List<OrderDto> getUserOrders(Long userId);
    Page<OrderDto> getUserOrders(Long userId, Pageable pageable);
    OrderDto getOrderById(Long orderId, Long userId);
    OrderDto updateOrderStatus(Long orderId, OrderStatus status, Long userId);
    List<OrderDto> getAllOrders();
    Page<OrderDto> getAllOrders(Pageable pageable);
    List<OrderDto> getSellerOrders(Long sellerUserId);
    Page<OrderDto> getSellerOrders(Long sellerUserId, Pageable pageable);
}
