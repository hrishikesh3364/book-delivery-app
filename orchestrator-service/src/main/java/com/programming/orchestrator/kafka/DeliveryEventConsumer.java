package com.programming.orchestrator.kafka;

import com.programming.DeliveryEvent;
import com.programming.orchestrator.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventConsumer {

    private final OrchestratorService orchestratorService;

    @KafkaListener(topics = "delivery-events", groupId = "orchestrator-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeDeliveryEvent(DeliveryEvent event) {
        log.info("Orchestrator received DeliveryEvent: {}", event);
        orchestratorService.handleDeliveryEvent(event);
    }
}
