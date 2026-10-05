package com.stylemirror.service;

import com.stylemirror.dto.TryOnAssetDto;

public interface TryOnService {
    TryOnAssetDto getTryOnAsset(Long productId);
    TryOnAssetDto saveOrUpdateAsset(Long productId, TryOnAssetDto dto, Long userId);
}
