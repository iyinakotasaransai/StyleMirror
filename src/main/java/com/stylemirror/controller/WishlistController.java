package com.stylemirror.controller;

import com.stylemirror.dto.ApiResponse;
import com.stylemirror.dto.WishlistDto;
import com.stylemirror.security.UserPrincipal;
import com.stylemirror.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<WishlistDto>> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        WishlistDto wishlist = wishlistService.getWishlist(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(wishlist));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistDto>> addToWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WishlistDto wishlist = wishlistService.addProductToWishlist(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Product added to wishlist", wishlist));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistDto>> removeFromWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        WishlistDto wishlist = wishlistService.removeProductFromWishlist(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Product removed from wishlist", wishlist));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        boolean inWishlist = wishlistService.isProductInWishlist(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("inWishlist", inWishlist)));
    }
}
