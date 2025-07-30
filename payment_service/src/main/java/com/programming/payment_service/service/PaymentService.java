package com.programming.payment_service.service;

import com.programming.OrderEvent;
import com.programming.PaymentEvent;
import com.programming.enums.PaymentStatus;
import com.programming.payment_service.dto.PaymentDetailsResponse;
import com.programming.payment_service.entity.Payment;
import com.programming.payment_service.exceptions.InvalidPaymentException;
import com.programming.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private static final String PAYMENT_TOPIC = "payment-events";

    @Transactional
    public void processPayment(OrderEvent orderEvent) {
        if (orderEvent.getAmount() <= 0) {
            throw new InvalidPaymentException("Invalid payment amount for order " + orderEvent.getOrderId());
        }

        Payment payment = Payment.builder()
                .orderId(orderEvent.getOrderId())
                .amount(orderEvent.getAmount())
                .status(PaymentStatus.SUCCESS)
                .build();

        paymentRepository.save(payment);

        PaymentEvent paymentEvent = PaymentEvent.builder()
                .orderId(orderEvent.getOrderId())
                .amount(orderEvent.getAmount())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        log.info("[OrderID: {}] Payment processed successfully", orderEvent.getOrderId());
        kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent);
    }


    @Transactional
    public void refundPayment(String orderId) {
        log.info("Refunding payment for order: {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order " + orderId));

        payment.setStatus(PaymentStatus.REFUND);
        paymentRepository.save(payment);

        PaymentEvent refundEvent = PaymentEvent.builder()
                .orderId(orderId)
                .amount(payment.getAmount())
                .paymentStatus(PaymentStatus.REFUND)
                .build();

        log.info("Publishing refund event: {}", refundEvent);
        kafkaTemplate.send(PAYMENT_TOPIC, refundEvent);
    }

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order " + orderId));

        return PaymentDetailsResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name()) // convert enum to String for DTO
                .build();
    }

    @Transactional(readOnly = true)
    public List<PaymentDetailsResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(payment -> PaymentDetailsResponse.builder()
                        .paymentId(payment.getId())
                        .orderId(payment.getOrderId())
                        .amount(payment.getAmount())
                        .status(payment.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }
}
