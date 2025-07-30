package com.programming.order_service.kafkaConsumer;

import com.programming.enums.OrderStatus;
import com.programming.OrderEvent;
import com.programming.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "order-updated-events", groupId = "order-service-group")
    public void handleOrderUpdate(OrderEvent event) {
        log.info("Received order update: {}", event);
        orderRepository.findById(Long.valueOf(event.getOrderId()))
                .ifPresent(order -> {
                    order.setStatus(event.getStatus()); // both are OrderStatus enum
                    orderRepository.save(order);
                    log.info("Order {} updated to {}", event.getOrderId(), event.getStatus());
                });
    }

}
