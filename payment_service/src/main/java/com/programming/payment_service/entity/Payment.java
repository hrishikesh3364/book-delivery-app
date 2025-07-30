package com.programming.payment_service.entity;

import com.programming.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;   // SUCCESS, FAILED, REFUND
}
