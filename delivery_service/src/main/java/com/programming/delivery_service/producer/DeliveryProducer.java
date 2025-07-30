package com.programming.delivery_service.producer;

import com.programming.DeliveryEvent;
import com.programming.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryProducer {

    private final KafkaTemplate<String, DeliveryEvent> deliveryKafkaTemplate;
    private final KafkaTemplate<String, PaymentEvent> refundKafkaTemplate;

    private static final String DELIVERY_TOPIC = "delivery-events";
    private static final String PAYMENT_TOPIC = "payment-events";

    public void sendDeliveryEvent(DeliveryEvent event) {
        log.info("Sending delivery event: {}", event);
        deliveryKafkaTemplate.send(DELIVERY_TOPIC, event);
    }

    public void sendRefundEvent(PaymentEvent event) {
        log.info("Sending refund event: {}", event);
        refundKafkaTemplate.send(PAYMENT_TOPIC, event);
    }
}
