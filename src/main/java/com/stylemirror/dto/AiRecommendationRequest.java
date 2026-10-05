package com.stylemirror.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecommendationRequest {

    private String requestId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private Long variantId;
    private String size;
    private String color;
    private String occasion;
    private String season;
    private String style;
    private String userContext;
}
