package com.stylemirror.service;

import com.stylemirror.dto.CartDto;
import com.stylemirror.dto.CartItemRequest;

public interface CartService {
    CartDto getCart(Long userId);
    CartDto addItemToCart(Long userId, CartItemRequest request);
    CartDto updateItemQuantity(Long userId, Long itemId, Integer quantity);
    CartDto removeItem(Long userId, Long itemId);
    void clearCart(Long userId);
}
