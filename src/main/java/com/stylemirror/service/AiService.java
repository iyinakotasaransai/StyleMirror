package com.stylemirror.service;

import com.stylemirror.dto.AiRecommendationRequest;
import com.stylemirror.dto.AiRecommendationResponse;

public interface AiService {
    AiRecommendationResponse getRecommendation(Long userId, AiRecommendationRequest request);
    AiRecommendationResponse getCachedRecommendation(String requestId);
}
