package com.stylemirror.service.impl;

import com.stylemirror.dto.CartDto;
import com.stylemirror.dto.CartItemDto;
import com.stylemirror.dto.CartItemRequest;
import com.stylemirror.entity.*;
import com.stylemirror.exception.BadRequestException;
import com.stylemirror.exception.ResourceNotFoundException;
import com.stylemirror.repository.*;
import com.stylemirror.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartDto getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToDto(cart);
    }

    @Override
    @Transactional
    public CartDto addItemToCart(Long userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        if (!Boolean.TRUE.equals(variant.getActive()) || !Boolean.TRUE.equals(variant.getProduct().getActive())) {
            throw new BadRequestException("This product or variant is currently unavailable");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), variant.getId());

        int targetQuantity = request.getQuantity();
        if (existingItemOpt.isPresent()) {
            targetQuantity += existingItemOpt.get().getQuantity();
        }

        if (targetQuantity > variant.getStock()) {
            throw new BadRequestException("Requested quantity (" + targetQuantity + ") exceeds available stock (" + variant.getStock() + ")");
        }

        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            existing.setQuantity(targetQuantity);
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        return mapToDto(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Override
    @Transactional
    public CartDto updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            return removeItem(userId, itemId);
        }

        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to your cart");
        }

        if (quantity > item.getProductVariant().getStock()) {
            throw new BadRequestException("Requested quantity (" + quantity + ") exceeds stock (" + item.getProductVariant().getStock() + ")");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return mapToDto(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Override
    @Transactional
    public CartDto removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to your cart");
        }

        cartItemRepository.delete(item);
        cart.getItems().removeIf(ci -> ci.getId().equals(itemId));

        return mapToDto(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    return cartRepository.save(Cart.builder().user(user).build());
                });
    }

    public CartDto mapToDto(Cart cart) {
        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        for (CartItem item : items) {
            ProductVariant pv = item.getProductVariant();
            Product p = pv.getProduct();
            BigDecimal itemSubtotal = pv.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);
            totalItems += item.getQuantity();

            String imgUrl = (p.getImages() != null && !p.getImages().isEmpty())
                    ? p.getImages().get(0).getImageUrl()
                    : "/assets/products/placeholder.svg";

            itemDtos.add(CartItemDto.builder()
                    .id(item.getId())
                    .variantId(pv.getId())
                    .productId(p.getId())
                    .productName(p.getName())
                    .brand(p.getBrand())
                    .size(pv.getSize())
                    .color(pv.getColor())
                    .sku(pv.getSku())
                    .unitPrice(pv.getPrice())
                    .quantity(item.getQuantity())
                    .availableStock(pv.getStock())
                    .subtotal(itemSubtotal)
                    .imageUrl(imgUrl)
                    .build());
        }

        BigDecimal taxRate = new BigDecimal("0.08"); // 8% simulated sales tax
        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = subtotal.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(new BigDecimal("100")) < 0
                ? new BigDecimal("10.00")
                : BigDecimal.ZERO; // Free shipping over $100
        BigDecimal grandTotal = subtotal.add(tax).add(shipping);

        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemDtos)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .estimatedTax(tax)
                .shippingFee(shipping)
                .grandTotal(grandTotal)
                .build();
    }
}
