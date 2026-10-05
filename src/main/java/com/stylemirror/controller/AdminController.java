package com.stylemirror.controller;

import com.stylemirror.dto.*;
import com.stylemirror.entity.Seller;
import com.stylemirror.enums.ApprovalStatus;
import com.stylemirror.repository.SellerRepository;
import com.stylemirror.service.AdminService;
import com.stylemirror.service.OrderService;
import com.stylemirror.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;
    private final OrderService orderService;
    private final SellerRepository sellerRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getDashboard()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getAllUsers()));
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<UserDto>> toggleUser(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload
    ) {
        boolean active = payload.getOrDefault("active", true);
        UserDto updated = adminService.toggleUserStatus(id, active);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
    }

    @GetMapping("/sellers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSellers() {
        List<Seller> sellers = sellerRepository.findAll();
        List<Map<String, Object>> response = sellers.stream().map(s -> Map.<String, Object>of(
                "id", s.getId(),
                "userId", s.getUser().getId(),
                "userName", s.getUser().getFullName(),
                "email", s.getUser().getEmail(),
                "storeName", s.getStoreName(),
                "storeDescription", s.getStoreDescription() != null ? s.getStoreDescription() : "",
                "businessAddress", s.getBusinessAddress(),
                "gstNumber", s.getGstNumber() != null ? s.getGstNumber() : "",
                "approvalStatus", s.getApprovalStatus().name(),
                "createdAt", s.getCreatedAt()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/sellers/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveSeller(@PathVariable Long id) {
        adminService.approveSeller(id);
        return ResponseEntity.ok(ApiResponse.ok("Seller approved successfully", null));
    }

    @PutMapping("/sellers/{id}/block")
    public ResponseEntity<ApiResponse<Void>> blockSeller(@PathVariable Long id) {
        adminService.blockSeller(id);
        return ResponseEntity.ok(ApiResponse.ok("Seller blocked successfully", null));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProducts() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getAllProducts()));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrders() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getAllOrders()));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.getDashboard()));
    }
}
