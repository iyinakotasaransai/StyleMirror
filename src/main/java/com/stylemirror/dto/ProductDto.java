package com.stylemirror.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    private Long sellerId;
    private String sellerStoreName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String brand;
    private BigDecimal basePrice;
    private Boolean active;
    private List<ProductVariantDto> variants;
    private List<String> imageUrls;
    private TryOnAssetDto tryOnAsset;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
