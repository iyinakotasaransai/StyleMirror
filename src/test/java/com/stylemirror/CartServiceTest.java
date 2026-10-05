package com.stylemirror;

import com.stylemirror.dto.CartDto;
import com.stylemirror.dto.CartItemRequest;
import com.stylemirror.entity.ProductVariant;
import com.stylemirror.entity.User;
import com.stylemirror.repository.ProductVariantRepository;
import com.stylemirror.repository.UserRepository;
import com.stylemirror.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Test
    @DisplayName("Should add garment variant to user cart and update quantity")
    void testCartOperations() {
        User buyer = userRepository.findByEmail("buyer@stylemirror.com")
                .orElseThrow(() -> new IllegalStateException("Seed buyer not found"));

        List<ProductVariant> variants = productVariantRepository.findAll();
        assertFalse(variants.isEmpty(), "Seed variants must exist");
        ProductVariant variant = variants.get(0);

        // 1. Add item
        CartItemRequest request = CartItemRequest.builder()
                .variantId(variant.getId())
                .quantity(2)
                .build();

        CartDto cart = cartService.addItemToCart(buyer.getId(), request);
        assertNotNull(cart);
        assertNotNull(cart.getItems());
        assertFalse(cart.getItems().isEmpty());

        Long itemId = cart.getItems().get(0).getId();

        // 2. Update quantity
        CartDto updatedCart = cartService.updateItemQuantity(buyer.getId(), itemId, 3);
        assertNotNull(updatedCart);
        assertEquals(3, updatedCart.getItems().get(0).getQuantity());

        // 3. Remove item
        CartDto clearedCart = cartService.removeItem(buyer.getId(), itemId);
        assertTrue(clearedCart.getItems().stream().noneMatch(i -> i.getId().equals(itemId)));
    }
}
