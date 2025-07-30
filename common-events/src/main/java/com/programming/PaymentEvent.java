package com.programming;

import com.programming.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String orderId;
    private double amount;
    private PaymentStatus paymentStatus;
}
