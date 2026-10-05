package com.stylemirror.repository;

import com.stylemirror.entity.TryOnAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TryOnAssetRepository extends JpaRepository<TryOnAsset, Long> {
    Optional<TryOnAsset> findByProductId(Long productId);
    Optional<TryOnAsset> findByProductIdAndEnabledTrue(Long productId);
}
