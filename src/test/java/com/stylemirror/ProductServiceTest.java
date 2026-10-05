package com.stylemirror;

import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.ProductVariantDto;
import com.stylemirror.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("Should retrieve all seeded active products")
    void testGetAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        assertNotNull(products);
        assertFalse(products.isEmpty(), "Product catalog should have seeded items");
    }

    @Test
    @DisplayName("Should retrieve product by valid ID with variants and try-on asset")
    void testGetProductById() {
        List<ProductDto> all = productService.getAllProducts();
        assertFalse(all.isEmpty());

        Long firstId = all.get(0).getId();
        ProductDto product = productService.getProductById(firstId);

        assertNotNull(product);
        assertEquals(firstId, product.getId());
        assertNotNull(product.getName());
        assertNotNull(product.getVariants());
        assertFalse(product.getVariants().isEmpty(), "Product should have variants");
    }

    @Test
    @DisplayName("Should search products by matching name keyword")
    void testSearchProducts() {
        List<ProductDto> results = productService.searchProducts("Velvet");
        assertNotNull(results);
        assertTrue(results.size() >= 1, "Should find at least 1 matching garment for 'Velvet'");
    }

    @Test
    @DisplayName("Should retrieve variants for a given product")
    void testGetProductVariants() {
        List<ProductDto> all = productService.getAllProducts();
        Long firstId = all.get(0).getId();

        List<ProductVariantDto> variants = productService.getProductVariants(firstId);
        assertNotNull(variants);
        assertFalse(variants.isEmpty());
        assertNotNull(variants.get(0).getSku());
    }
}
