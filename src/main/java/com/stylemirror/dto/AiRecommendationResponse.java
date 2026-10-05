package com.stylemirror.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecommendationResponse {

    private String requestId;
    private String recommendation;
    private String spokenText;
    private String reason;
    private String mood;
    private String animation; // idle, thinking, thumbsUp, nod, shakeHead, wave, speaking
    private List<Long> recommendedProductIds;
    private boolean cached;
}
