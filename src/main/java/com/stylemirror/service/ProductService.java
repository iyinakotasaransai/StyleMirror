package com.stylemirror.service;

import com.stylemirror.dto.ProductCreateDto;
import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.ProductVariantDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    Page<ProductDto> getAllProducts(Pageable pageable);
    ProductDto getProductById(Long id);
    List<ProductDto> searchProducts(String query);
    Page<ProductDto> searchProducts(String query, Pageable pageable);
    List<ProductDto> getProductsByCategory(Long categoryId);

    ProductDto createProduct(ProductCreateDto dto, Long userId);
    ProductDto updateProduct(Long id, ProductCreateDto dto, Long userId);
    void deleteProduct(Long id, Long userId);

    List<ProductVariantDto> getProductVariants(Long productId);
    ProductVariantDto addProductVariant(Long productId, ProductVariantDto dto, Long userId);
    ProductVariantDto updateProductVariant(Long variantId, ProductVariantDto dto, Long userId);
    void deleteProductVariant(Long variantId, Long userId);
}
