package com.programming.payment_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDetailsResponse {
    private Long paymentId;
    private String orderId;
    private double amount;
    private String status;
}
