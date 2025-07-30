package com.programming.payment_service.kafka;

import com.programming.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private static final String TOPIC_NAME = "payment-events";

    public void sendPaymentStatus(PaymentEvent event) {
        log.info("Publishing payment status event: {}", event);
        kafkaTemplate.send(TOPIC_NAME, event);
    }
}
