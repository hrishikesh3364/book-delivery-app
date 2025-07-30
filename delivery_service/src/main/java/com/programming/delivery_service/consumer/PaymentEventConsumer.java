package com.programming.delivery_service.consumer;

import com.programming.PaymentEvent;
import com.programming.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final DeliveryService deliveryService;

    @KafkaListener(
            topics = "payment-events",
            groupId = "delivery-service-group",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    public void consumePaymentEvent(PaymentEvent event) {
        log.info("Received PaymentEvent in Delivery service: {}", event);
        deliveryService.handlePayment(event);
    }
}
