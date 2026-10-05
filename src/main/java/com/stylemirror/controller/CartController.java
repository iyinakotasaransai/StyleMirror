package com.stylemirror.controller;

import com.stylemirror.dto.ApiResponse;
import com.stylemirror.dto.CartDto;
import com.stylemirror.dto.CartItemRequest;
import com.stylemirror.security.UserPrincipal;
import com.stylemirror.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        CartDto cart = cartService.getCart(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CartDto cart = cartService.addItemToCart(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cart));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDto>> updateQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> payload,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Integer quantity = payload.get("quantity");
        CartDto cart = cartService.updateItemQuantity(principal.getId(), id, quantity);
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cart));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CartDto cart = cartService.removeItem(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }
}
