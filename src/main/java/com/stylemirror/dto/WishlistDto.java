package com.stylemirror.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistDto {

    private Long id;
    private Long userId;

    @Builder.Default
    private List<ProductDto> products = new ArrayList<>();

    private Integer totalItems;
}
