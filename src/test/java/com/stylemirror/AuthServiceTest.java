package com.stylemirror;

import com.stylemirror.dto.AuthRequest;
import com.stylemirror.dto.AuthResponse;
import com.stylemirror.dto.RegisterRequest;
import com.stylemirror.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Should successfully authenticate admin demo account")
    void testAdminLogin() {
        AuthRequest request = AuthRequest.builder()
                .email("admin@stylemirror.com")
                .password("Admin@123")
                .build();

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("admin@stylemirror.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Should successfully register a new buyer")
    void testBuyerRegistration() {
        String uniqueEmail = "buyer_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Test Customer")
                .email(uniqueEmail)
                .password("SecurePass123!")
                .phone("+1-555-9999")
                .role("BUYER")
                .build();

        AuthResponse response = authService.register(request);
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals(uniqueEmail, response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_BUYER"));
    }
}
