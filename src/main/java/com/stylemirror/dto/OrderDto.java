package com.stylemirror.dto;

import com.stylemirror.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private Long id;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private String userName;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String contactPhone;
    private OrderStatus status;

    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();

    // Simulated payment details
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
