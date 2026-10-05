package com.stylemirror.controller;

import com.stylemirror.dto.ApiResponse;
import com.stylemirror.dto.OrderDto;
import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.SellerDashboardDto;
import com.stylemirror.security.UserPrincipal;
import com.stylemirror.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<SellerDashboardDto>> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        SellerDashboardDto dashboard = sellerService.getDashboard(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProducts(@AuthenticationPrincipal UserPrincipal principal) {
        List<ProductDto> products = sellerService.getSellerProducts(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrders(@AuthenticationPrincipal UserPrincipal principal) {
        List<OrderDto> orders = sellerService.getSellerOrders(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<SellerDashboardDto>> getStatistics(@AuthenticationPrincipal UserPrincipal principal) {
        SellerDashboardDto stats = sellerService.getDashboard(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
