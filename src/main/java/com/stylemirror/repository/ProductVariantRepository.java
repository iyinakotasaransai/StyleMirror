package com.stylemirror.repository;

import com.stylemirror.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
    List<ProductVariant> findByProductIdAndActiveTrue(Long productId);
    Optional<ProductVariant> findBySku(String sku);
    Boolean existsBySku(String sku);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.seller.id = :sellerId AND pv.stock <= :threshold")
    List<ProductVariant> findLowStockVariantsBySeller(@Param("sellerId") Long sellerId, @Param("threshold") Integer threshold);
}
