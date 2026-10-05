package com.stylemirror.service;

import com.stylemirror.dto.WishlistDto;

public interface WishlistService {
    WishlistDto getWishlist(Long userId);
    WishlistDto addProductToWishlist(Long userId, Long productId);
    WishlistDto removeProductFromWishlist(Long userId, Long productId);
    boolean isProductInWishlist(Long userId, Long productId);
}
