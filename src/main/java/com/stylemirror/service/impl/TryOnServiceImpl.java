package com.stylemirror.service.impl;

import com.stylemirror.dto.TryOnAssetDto;
import com.stylemirror.entity.Product;
import com.stylemirror.entity.Seller;
import com.stylemirror.entity.TryOnAsset;
import com.stylemirror.entity.User;
import com.stylemirror.enums.RoleType;
import com.stylemirror.exception.ForbiddenException;
import com.stylemirror.exception.ResourceNotFoundException;
import com.stylemirror.repository.ProductRepository;
import com.stylemirror.repository.SellerRepository;
import com.stylemirror.repository.TryOnAssetRepository;
import com.stylemirror.repository.UserRepository;
import com.stylemirror.service.TryOnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TryOnServiceImpl implements TryOnService {

    private final TryOnAssetRepository tryOnAssetRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public TryOnAssetDto getTryOnAsset(Long productId) {
        TryOnAsset asset = tryOnAssetRepository.findByProductIdAndEnabledTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No active Virtual Try-On asset found for product ID: " + productId));

        return TryOnAssetDto.builder()
                .id(asset.getId())
                .productId(asset.getProduct().getId())
                .overlayImagePath(asset.getOverlayImagePath())
                .glbPath(asset.getGlbPath())
                .enabled(asset.getEnabled())
                .build();
    }

    @Override
    @Transactional
    public TryOnAssetDto saveOrUpdateAsset(Long productId, TryOnAssetDto dto, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName() == RoleType.ROLE_ADMIN);
        if (!isAdmin) {
            Seller seller = sellerRepository.findByUserId(userId)
                    .orElseThrow(() -> new ForbiddenException("You are not registered as a seller"));
            if (!product.getSeller().getId().equals(seller.getId())) {
                throw new ForbiddenException("You do not own this product");
            }
        }

        TryOnAsset asset = tryOnAssetRepository.findByProductId(productId)
                .orElse(TryOnAsset.builder().product(product).build());

        asset.setOverlayImagePath(dto.getOverlayImagePath());
        asset.setGlbPath(dto.getGlbPath());
        if (dto.getEnabled() != null) {
            asset.setEnabled(dto.getEnabled());
        }

        TryOnAsset saved = tryOnAssetRepository.save(asset);

        return TryOnAssetDto.builder()
                .id(saved.getId())
                .productId(product.getId())
                .overlayImagePath(saved.getOverlayImagePath())
                .glbPath(saved.getGlbPath())
                .enabled(saved.getEnabled())
                .build();
    }
}
