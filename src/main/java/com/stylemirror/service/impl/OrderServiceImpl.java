package com.stylemirror.service.impl;

import com.stylemirror.dto.OrderCreateRequest;
import com.stylemirror.dto.OrderDto;
import com.stylemirror.dto.OrderItemDto;
import com.stylemirror.entity.*;
import com.stylemirror.enums.OrderStatus;
import com.stylemirror.enums.PaymentStatus;
import com.stylemirror.enums.RoleType;
import com.stylemirror.exception.BadRequestException;
import com.stylemirror.exception.ForbiddenException;
import com.stylemirror.exception.ResourceNotFoundException;
import com.stylemirror.repository.*;
import com.stylemirror.service.CartService;
import com.stylemirror.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Shopping cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot create order from an empty cart");
        }

        // Validate stock and compute authoritative prices
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        for (CartItem ci : cartItems) {
            ProductVariant pv = productVariantRepository.findById(ci.getProductVariant().getId())
                    .orElseThrow(() -> new BadRequestException("Product variant no longer available"));

            if (pv.getStock() < ci.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + pv.getProduct().getName() +
                        " (" + pv.getSize() + " / " + pv.getColor() + "). Available: " + pv.getStock());
            }

            // Deduct stock atomically
            pv.setStock(pv.getStock() - ci.getQuantity());
            productVariantRepository.save(pv);

            // Calculate historical snapshot price
            BigDecimal unitPrice = pv.getPrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);

            OrderItem orderItem = OrderItem.builder()
                    .productVariant(pv)
                    .productName(pv.getProduct().getName())
                    .size(pv.getSize())
                    .color(pv.getColor())
                    .unitPrice(unitPrice)
                    .quantity(ci.getQuantity())
                    .subtotal(itemSubtotal)
                    .build();

            orderItemsToSave.add(orderItem);
        }

        // Calculate tax and total
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = subtotal.compareTo(new BigDecimal("100")) < 0 ? new BigDecimal("10.00") : BigDecimal.ZERO;
        BigDecimal grandTotal = subtotal.add(tax).add(shipping);

        // Generate human-readable order number
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String shortId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String orderNumber = "ORD-" + datePrefix + "-" + shortId;

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .totalAmount(grandTotal)
                .shippingAddress(request.getShippingAddress().trim())
                .contactPhone(request.getContactPhone().trim())
                .status(OrderStatus.PROCESSING)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Attach saved order to items and persist
        for (OrderItem item : orderItemsToSave) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItemsToSave);
        savedOrder.setItems(orderItemsToSave);

        // Create simulated payment
        String txnId = "TXN-SIM-" + datePrefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment = Payment.builder()
                .order(savedOrder)
                .paymentMethod(request.getPaymentMethod())
                .transactionId(txnId)
                .amount(grandTotal)
                .status(PaymentStatus.PAID)
                .build();
        paymentRepository.save(payment);
        savedOrder.setPayment(payment);

        // Clear user cart after checkout
        cartService.clearCart(userId);

        return mapToDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getUserOrders(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        verifyOrderAccess(order, userId);
        return mapToDto(order);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName() == RoleType.ROLE_ADMIN);
        boolean isSeller = user.getRoles().stream().anyMatch(r -> r.getName() == RoleType.ROLE_SELLER);

        if (!isAdmin && !isSeller) {
            throw new ForbiddenException("Only sellers and administrators can update order status");
        }

        order.setStatus(status);
        return mapToDto(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getSellerOrders(Long sellerUserId) {
        Seller seller = sellerRepository.findByUserId(sellerUserId)
                .orElseThrow(() -> new ForbiddenException("User is not an active seller"));
        return orderRepository.findOrdersBySellerId(seller.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getSellerOrders(Long sellerUserId, Pageable pageable) {
        Seller seller = sellerRepository.findByUserId(sellerUserId)
                .orElseThrow(() -> new ForbiddenException("User is not an active seller"));
        return orderRepository.findOrdersBySellerId(seller.getId(), pageable)
                .map(this::mapToDto);
    }

    private void verifyOrderAccess(Order order, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName() == RoleType.ROLE_ADMIN);
        if (isAdmin) {
            return;
        }

        if (order.getUser().getId().equals(userId)) {
            return; // Buyer owns the order
        }

        // Check if seller has items in this order
        Seller seller = sellerRepository.findByUserId(userId).orElse(null);
        if (seller != null) {
            boolean hasSellerItem = order.getItems().stream()
                    .anyMatch(i -> i.getProductVariant() != null &&
                            i.getProductVariant().getProduct().getSeller().getId().equals(seller.getId()));
            if (hasSellerItem) {
                return;
            }
        }

        throw new ForbiddenException("You are not authorized to view this order");
    }

    private OrderDto mapToDto(Order order) {
        List<OrderItemDto> items = order.getItems() != null
                ? order.getItems().stream().map(i -> OrderItemDto.builder()
                        .id(i.getId())
                        .productVariantId(i.getProductVariant() != null ? i.getProductVariant().getId() : null)
                        .productName(i.getProductName())
                        .size(i.getSize())
                        .color(i.getColor())
                        .unitPrice(i.getUnitPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build()).collect(Collectors.toList())
                : new ArrayList<>();

        String paymentMethod = null;
        String paymentStatus = null;
        String txnId = null;

        if (order.getPayment() != null) {
            paymentMethod = order.getPayment().getPaymentMethod();
            paymentStatus = order.getPayment().getStatus().name();
            txnId = order.getPayment().getTransactionId();
        } else {
            paymentRepository.findByOrderId(order.getId()).ifPresent(p -> {});
        }

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .userName(order.getUser().getFullName())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .contactPhone(order.getContactPhone())
                .status(order.getStatus())
                .items(items)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .transactionId(txnId)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
