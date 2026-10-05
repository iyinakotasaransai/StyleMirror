package com.stylemirror.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TryOnAssetDto {

    private Long id;
    private Long productId;

    @NotBlank(message = "Overlay image path is required")
    private String overlayImagePath;

    private String glbPath;

    @Builder.Default
    private Boolean enabled = true;
}
