package com.stylemirror.controller;

import com.stylemirror.dto.ApiResponse;
import com.stylemirror.dto.ProductCreateDto;
import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.ProductVariantDto;
import com.stylemirror.security.UserPrincipal;
import com.stylemirror.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.ok(productService.searchProducts(search.trim())));
        }
        if (categoryId != null) {
            return ResponseEntity.ok(ApiResponse.ok(productService.getProductsByCategory(categoryId)));
        }
        return ResponseEntity.ok(ApiResponse.ok(productService.getAllProducts()));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProductById(id)));
    }

    @GetMapping("/products/search")
    public ResponseEntity<ApiResponse<List<ProductDto>>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.ok(productService.searchProducts(query)));
    }

    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProductsByCategory(categoryId)));
    }

    @PostMapping("/products")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody ProductCreateDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProductDto created = productService.createProduct(dto, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Product created successfully", created));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductCreateDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProductDto updated = productService.updateProduct(id, dto, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", updated));
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        productService.deleteProduct(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully", null));
    }

    // Product Variants APIs
    @GetMapping("/products/{productId}/variants")
    public ResponseEntity<ApiResponse<List<ProductVariantDto>>> getVariants(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getProductVariants(productId)));
    }

    @PostMapping("/products/{productId}/variants")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantDto>> addVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProductVariantDto created = productService.addProductVariant(productId, dto, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Variant added successfully", created));
    }

    @PutMapping("/variants/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductVariantDto>> updateVariant(
            @PathVariable Long id,
            @Valid @RequestBody ProductVariantDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ProductVariantDto updated = productService.updateProductVariant(id, dto, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Variant updated successfully", updated));
    }

    @DeleteMapping("/variants/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        productService.deleteProductVariant(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Variant removed successfully", null));
    }
}
