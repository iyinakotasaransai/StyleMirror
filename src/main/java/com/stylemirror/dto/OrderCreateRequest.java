package com.stylemirror.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    @NotBlank(message = "Payment method is required")
    @Builder.Default
    private String paymentMethod = "SIMULATED_CREDIT_CARD"; // SIMULATED_CREDIT_CARD, SIMULATED_UPI, COD
}
