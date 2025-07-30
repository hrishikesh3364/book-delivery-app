package com.programming.payment_service.kafka;

import com.programming.OrderEvent;
import com.programming.enums.OrderStatus;
import com.programming.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order-events",
            groupId = "payment-service-group",
            containerFactory = "orderKafkaListenerFactory")
    public void consumeOrderEvent(ConsumerRecord<String, OrderEvent> record, Acknowledgment acknowledgment) {
        OrderEvent event = record.value();
        log.info("Received order event for payment processing: {}", event);

        try {
            if (event.getStatus() == OrderStatus.CANCELLED || event.getStatus() == OrderStatus.COMPLETED) {
                log.info("Skipping payment for order {} as status is {}", event.getOrderId(), event.getStatus());
            } else {
                paymentService.processPayment(event);
            }
        } catch (Exception ex) {
            log.error("Error processing payment event: {}", event, ex);
            // Optionally don't ack here if you want retry
            acknowledgment.acknowledge();
            return;
        }


        acknowledgment.acknowledge();
    }
}
