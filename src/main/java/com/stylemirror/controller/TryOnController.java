package com.stylemirror.controller;

import com.stylemirror.dto.ApiResponse;
import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.TryOnAssetDto;
import com.stylemirror.security.UserPrincipal;
import com.stylemirror.service.ProductService;
import com.stylemirror.service.TryOnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tryon")
@RequiredArgsConstructor
public class TryOnController {

    private final TryOnService tryOnService;
    private final ProductService productService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<ProductDto>> getTryOnProduct(@PathVariable Long productId) {
        ProductDto product = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @GetMapping("/product/{productId}/asset")
    public ResponseEntity<ApiResponse<TryOnAssetDto>> getProductAsset(@PathVariable Long productId) {
        TryOnAssetDto asset = tryOnService.getTryOnAsset(productId);
        return ResponseEntity.ok(ApiResponse.ok(asset));
    }

    @PostMapping("/product/{productId}/asset")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TryOnAssetDto>> updateProductAsset(
            @PathVariable Long productId,
            @Valid @RequestBody TryOnAssetDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        TryOnAssetDto saved = tryOnService.saveOrUpdateAsset(productId, dto, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Try-on asset updated successfully", saved));
    }
}
