package com.stylemirror.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Boolean active;
    private Set<String> roles;
    private Long sellerId;
    private String sellerStatus;
    private LocalDateTime createdAt;
}
