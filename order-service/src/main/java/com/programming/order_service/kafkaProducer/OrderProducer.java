package com.programming.order_service.kafkaProducer;

import com.programming.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private static final String TOPIC_NAME = "order-events";

    public void sendOrderEvent(OrderEvent event) {
        log.info("Sending OrderEvent: {}", event);
        kafkaTemplate.send(TOPIC_NAME, event);
    }
}
