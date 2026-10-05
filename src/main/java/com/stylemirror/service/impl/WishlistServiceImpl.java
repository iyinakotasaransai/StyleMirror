package com.stylemirror.service.impl;

import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.WishlistDto;
import com.stylemirror.entity.Product;
import com.stylemirror.entity.User;
import com.stylemirror.entity.Wishlist;
import com.stylemirror.entity.WishlistItem;
import com.stylemirror.exception.ResourceNotFoundException;
import com.stylemirror.repository.ProductRepository;
import com.stylemirror.repository.UserRepository;
import com.stylemirror.repository.WishlistItemRepository;
import com.stylemirror.repository.WishlistRepository;
import com.stylemirror.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductServiceImpl productService;

    @Override
    @Transactional
    public WishlistDto getWishlist(Long userId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        return mapToDto(wishlist);
    }

    @Override
    @Transactional
    public WishlistDto addProductToWishlist(Long userId, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), product.getId())) {
            WishlistItem item = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .build();
            wishlistItemRepository.save(item);
        }

        return getWishlist(userId);
    }

    @Override
    @Transactional
    public WishlistDto removeProductFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
        return getWishlist(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long userId, Long productId) {
        return wishlistRepository.findByUserId(userId)
                .map(w -> wishlistItemRepository.existsByWishlistIdAndProductId(w.getId(), productId))
                .orElse(false);
    }

    private Wishlist getOrCreateWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    return wishlistRepository.save(Wishlist.builder().user(user).build());
                });
    }

    private WishlistDto mapToDto(Wishlist wishlist) {
        List<WishlistItem> items = wishlistItemRepository.findByWishlistId(wishlist.getId());
        List<ProductDto> products = items.stream()
                .map(item -> productService.mapToDto(item.getProduct()))
                .collect(Collectors.toList());

        return WishlistDto.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .products(products)
                .totalItems(products.size())
                .build();
    }
}
