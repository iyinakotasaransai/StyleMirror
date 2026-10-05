package com.stylemirror.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long id;
    private String email;
    private String fullName;
    private Set<String> roles;
    private Long sellerId;
    private String sellerStatus;
}
