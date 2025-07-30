package com.programming.orchestrator.kafka;

import com.programming.OrderEvent;
import com.programming.orchestrator.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final OrchestratorService orchestratorService;

    @KafkaListener(topics = "order-events", groupId = "orchestrator-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("Orchestrator received OrderEvent: {}", event);
        orchestratorService.handleOrder(event);
    }
}
