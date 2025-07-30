package com.programming.payment_service.kafka;

import com.programming.DeliveryEvent;
import com.programming.enums.DeliveryStatus;
import com.programming.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "delivery-events", groupId = "payment-service-group")
    public void consumeDeliveryEvent(DeliveryEvent deliveryEvent) {
        log.info("Received DeliveryEvent: {}", deliveryEvent);

        // Use enum for comparison
        if (deliveryEvent.getStatus() == DeliveryStatus.FAILED) {
            paymentService.refundPayment(deliveryEvent.getOrderId());
            log.info("Refund triggered for order {}", deliveryEvent.getOrderId());
        } else {
            log.info("Delivery status {} - no refund required", deliveryEvent.getStatus());
        }
    }
}
