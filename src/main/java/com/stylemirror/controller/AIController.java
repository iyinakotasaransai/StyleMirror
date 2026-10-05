package com.stylemirror.controller;

import com.stylemirror.dto.AiRecommendationRequest;
import com.stylemirror.dto.AiRecommendationResponse;
import com.stylemirror.dto.ApiResponse;
import com.stylemirror.security.UserPrincipal;
import com.stylemirror.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AiService aiService;

    @PostMapping({"/recommendation", "/recommend"})
    public ResponseEntity<ApiResponse<AiRecommendationResponse>> getRecommendation(
            @Valid @RequestBody AiRecommendationRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal != null ? principal.getId() : 1L;
        AiRecommendationResponse response = aiService.getRecommendation(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/recommendation/{requestId}")
    public ResponseEntity<ApiResponse<AiRecommendationResponse>> getCachedRecommendation(@PathVariable String requestId) {
        AiRecommendationResponse response = aiService.getCachedRecommendation(requestId);
        if (response == null) {
            return ResponseEntity.status(404).body(ApiResponse.error("No cached recommendation found for request ID: " + requestId));
        }
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
