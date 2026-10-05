package com.stylemirror;

import com.stylemirror.dto.CartItemRequest;
import com.stylemirror.dto.OrderCreateRequest;
import com.stylemirror.dto.OrderDto;
import com.stylemirror.entity.ProductVariant;
import com.stylemirror.entity.User;
import com.stylemirror.enums.OrderStatus;
import com.stylemirror.repository.ProductVariantRepository;
import com.stylemirror.repository.UserRepository;
import com.stylemirror.service.CartService;
import com.stylemirror.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Test
    @DisplayName("Should create order from user cart items and update order lifecycle status")
    void testOrderCreationAndStatusUpdate() {
        User buyer = userRepository.findByEmail("buyer@stylemirror.com")
                .orElseThrow(() -> new IllegalStateException("Seed buyer not found"));
        User admin = userRepository.findByEmail("admin@stylemirror.com")
                .orElseThrow(() -> new IllegalStateException("Seed admin not found"));

        List<ProductVariant> variants = productVariantRepository.findAll();
        assertFalse(variants.isEmpty(), "Seed variants must exist");
        ProductVariant variant = variants.get(0);

        // Populate cart first
        cartService.addItemToCart(buyer.getId(), CartItemRequest.builder()
                .variantId(variant.getId())
                .quantity(1)
                .build());

        // Create Order
        OrderCreateRequest orderReq = OrderCreateRequest.builder()
                .shippingAddress("742 Evergreen Terrace, Springfield, OR")
                .contactPhone("+1-555-0199")
                .paymentMethod("SIMULATED_CREDIT_CARD")
                .build();

        OrderDto order = orderService.createOrder(buyer.getId(), orderReq);
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        assertNotNull(order.getItems());
        assertFalse(order.getItems().isEmpty());

        // Update Order Status as admin
        OrderDto updated = orderService.updateOrderStatus(order.getId(), OrderStatus.SHIPPED, admin.getId());
        assertEquals(OrderStatus.SHIPPED, updated.getStatus());
    }
}
