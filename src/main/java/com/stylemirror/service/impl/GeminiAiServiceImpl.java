package com.stylemirror.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stylemirror.dto.AiRecommendationRequest;
import com.stylemirror.dto.AiRecommendationResponse;
import com.stylemirror.entity.AISuggestionLog;
import com.stylemirror.entity.Product;
import com.stylemirror.entity.User;
import com.stylemirror.exception.BadRequestException;
import com.stylemirror.repository.AISuggestionLogRepository;
import com.stylemirror.repository.ProductRepository;
import com.stylemirror.repository.UserRepository;
import com.stylemirror.service.AiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GeminiAiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiServiceImpl.class);

    @Value("${app.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${app.gemini.api-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String geminiApiUrl;

    @Value("${app.gemini.rate-limit-per-minute:10}")
    private int rateLimitPerMinute;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AISuggestionLogRepository aiSuggestionLogRepository;
    private final ObjectMapper objectMapper;

    // Rate limiting tracking: userId -> list of request timestamps in millis
    private final Map<Long, List<Long>> userRateLimits = new ConcurrentHashMap<>();

    // In-memory cache: cacheKey -> AiRecommendationResponse
    private final Map<String, AiRecommendationResponse> cache = new ConcurrentHashMap<>();

    // RequestId lookup cache
    private final Map<String, AiRecommendationResponse> requestIdCache = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public AiRecommendationResponse getRecommendation(Long userId, AiRecommendationRequest request) {
        checkRateLimit(userId);

        Product product = productRepository.findById(request.getProductId()).orElse(null);
        String productName = product != null ? product.getName() : "Fashion Garment";
        String brand = product != null ? product.getBrand() : "StyleMirror";
        String color = request.getColor() != null ? request.getColor() : "Default Color";
        String size = request.getSize() != null ? request.getSize() : "M";
        String occasion = request.getOccasion() != null ? request.getOccasion() : "Casual Everyday";

        String cacheKey = String.format("%d_%d_%s_%s_%s",
                userId, request.getProductId(), size, color, occasion);

        if (cache.containsKey(cacheKey)) {
            log.info("Returning cached AI recommendation for key: {}", cacheKey);
            AiRecommendationResponse cachedResponse = cache.get(cacheKey);
            cachedResponse.setCached(true);
            if (request.getRequestId() != null) {
                cachedResponse.setRequestId(request.getRequestId());
                requestIdCache.put(request.getRequestId(), cachedResponse);
            }
            return cachedResponse;
        }

        long startTime = System.currentTimeMillis();
        AiRecommendationResponse response;

        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.equals("YOUR_GEMINI_API_KEY_HERE")) {
            response = callGeminiApi(product, request, productName, brand, color, size, occasion);
        } else {
            log.info("Gemini API key is not configured. Generating intelligent local fashion styling response.");
            response = generateIntelligentFallback(productName, brand, color, size, occasion);
        }

        long responseTime = System.currentTimeMillis() - startTime;
        if (request.getRequestId() != null) {
            response.setRequestId(request.getRequestId());
            requestIdCache.put(request.getRequestId(), response);
        }

        cache.put(cacheKey, response);
        logAiSuggestion(userId, product, request, response, responseTime);

        return response;
    }

    @Override
    public AiRecommendationResponse getCachedRecommendation(String requestId) {
        return requestIdCache.get(requestId);
    }

    private void checkRateLimit(Long userId) {
        long now = System.currentTimeMillis();
        long windowStart = now - 60_000; // 1 minute window

        userRateLimits.compute(userId, (k, timestamps) -> {
            if (timestamps == null) {
                timestamps = new ArrayList<>();
            }
            // Remove timestamps older than 1 minute
            timestamps.removeIf(ts -> ts < windowStart);
            if (timestamps.size() >= rateLimitPerMinute) {
                throw new BadRequestException("AI recommendation rate limit exceeded (maximum " +
                        rateLimitPerMinute + " requests per minute). Please try again shortly.");
            }
            timestamps.add(now);
            return timestamps;
        });
    }

    private AiRecommendationResponse callGeminiApi(Product product, AiRecommendationRequest request,
                                                    String productName, String brand, String color, String size, String occasion) {
        try {
            String prompt = String.format(
                    "You are StyleMirror's elite AI Fashion Stylist and Virtual Try-On Assistant. " +
                    "Analyze this garment and user try-on context:\n" +
                    "- Product: %s by %s\n" +
                    "- Color: %s\n" +
                    "- Size: %s\n" +
                    "- Occasion: %s\n" +
                    "- User Note: %s\n\n" +
                    "Respond with ONLY a raw JSON object (no markdown, no ```json formatting) with these exact keys:\n" +
                    "{\n" +
                    "  \"recommendation\": \"Detailed style advice explaining fit, complementary pants/shoes, and styling tips.\",\n" +
                    "  \"spokenText\": \"A crisp, friendly, engaging 1-2 sentence recommendation suitable for text-to-speech voice.\",\n" +
                    "  \"reason\": \"Fashion rationale highlighting color harmony, silhouette, or seasonal versatility.\",\n" +
                    "  \"mood\": \"One of: confident, happy, elegant, bold\",\n" +
                    "  \"animation\": \"One of: thumbsUp, nod, wave, thinking\"\n" +
                    "}",
                    productName, brand, color, size, occasion,
                    request.getUserContext() != null ? request.getUserContext() : "Virtual mirror session"
            );

            Map<String, Object> part = Collections.singletonMap("text", prompt);
            Map<String, Object> content = Collections.singletonMap("parts", Collections.singletonList(part));
            Map<String, Object> requestBody = Collections.singletonMap("contents", Collections.singletonList(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String urlWithKey = geminiApiUrl + "?key=" + geminiApiKey.trim();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlWithKey, entity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                JsonNode root = objectMapper.readTree(responseEntity.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    String rawText = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                    return parseAiJson(rawText, productName, brand, color, size, occasion);
                }
            }
        } catch (Exception ex) {
            log.warn("Gemini API call encountered an error: {}. Falling back gracefully.", ex.getMessage());
        }

        return generateIntelligentFallback(productName, brand, color, size, occasion);
    }

    private AiRecommendationResponse parseAiJson(String text, String productName, String brand,
                                                 String color, String size, String occasion) {
        try {
            // Remove markdown code fences if present
            String cleaned = text.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            JsonNode node = objectMapper.readTree(cleaned);

            String rec = node.has("recommendation") ? node.get("recommendation").asText() : "";
            String spoken = node.has("spokenText") ? node.get("spokenText").asText() : rec;
            String reason = node.has("reason") ? node.get("reason").asText() : "Silhouette matches body landmarks.";
            String mood = node.has("mood") ? node.get("mood").asText() : "confident";
            String anim = node.has("animation") ? node.get("animation").asText() : "thumbsUp";

            return AiRecommendationResponse.builder()
                    .recommendation(rec)
                    .spokenText(spoken)
                    .reason(reason)
                    .mood(mood)
                    .animation(anim)
                    .recommendedProductIds(Arrays.asList(1L, 2L, 4L))
                    .cached(false)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to parse Gemini response JSON: {}. Using fallback.", e.getMessage());
            return generateIntelligentFallback(productName, brand, color, size, occasion);
        }
    }

    private AiRecommendationResponse generateIntelligentFallback(String product, String brand,
                                                                 String color, String size, String occasion) {
        String spoken = String.format(
                "The %s in %s complements your proportions gracefully. It delivers an effortless, premium aesthetic for %s.",
                product, color, occasion
        );

        String detailed = String.format(
                "The %s by %s is tailored with clean lines that flatter your torso landmarks. " +
                "The %s hue provides timeless versatility. For %s, style this with tailored slim trousers or dark denim, " +
                "finished with minimalist leather footwear.",
                product, brand, color, occasion
        );

        String reason = String.format(
                "Color %s establishes balanced contrast, while the %s fit ensures ergonomic drape across the shoulders and chest.",
                color, size
        );

        return AiRecommendationResponse.builder()
                .recommendation(detailed)
                .spokenText(spoken)
                .reason(reason)
                .mood("confident")
                .animation("thumbsUp")
                .recommendedProductIds(Arrays.asList(1L, 4L, 8L))
                .cached(false)
                .build();
    }

    private void logAiSuggestion(Long userId, Product product, AiRecommendationRequest request,
                                 AiRecommendationResponse response, long responseTimeMs) {
        try {
            User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
            String summary = String.format("Product: %s, Color: %s, Size: %s, Occasion: %s",
                    product != null ? product.getName() : request.getProductId(),
                    request.getColor(), request.getSize(), request.getOccasion());

            String responseJson = objectMapper.writeValueAsString(response);

            AISuggestionLog audit = AISuggestionLog.builder()
                    .user(user)
                    .product(product)
                    .requestSummary(summary)
                    .responseJson(responseJson)
                    .animation(response.getAnimation())
                    .mood(response.getMood())
                    .success(true)
                    .responseTimeMs(responseTimeMs)
                    .build();

            aiSuggestionLogRepository.save(audit);
        } catch (Exception ex) {
            log.warn("Failed to persist AI suggestion log: {}", ex.getMessage());
        }
    }
}
