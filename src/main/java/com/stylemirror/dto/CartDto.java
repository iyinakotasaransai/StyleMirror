package com.stylemirror.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {

    private Long id;
    private Long userId;

    @Builder.Default
    private List<CartItemDto> items = new ArrayList<>();

    private Integer totalItems;
    private BigDecimal subtotal;
    private BigDecimal estimatedTax;
    private BigDecimal shippingFee;
    private BigDecimal grandTotal;
}
