package com.stylemirror.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {

    private Long id;
    private Long variantId;
    private Long productId;
    private String productName;
    private String brand;
    private String size;
    private String color;
    private String sku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private Integer availableStock;
    private BigDecimal subtotal;
    private String imageUrl;
}
