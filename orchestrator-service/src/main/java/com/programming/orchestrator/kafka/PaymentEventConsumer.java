package com.programming.orchestrator.kafka;

import com.programming.PaymentEvent;
import com.programming.orchestrator.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final OrchestratorService orchestratorService;

    @KafkaListener(topics = "payment-events", groupId = "orchestrator-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentEvent(PaymentEvent event) {
        log.info("Orchestrator received PaymentEvent: {}", event);
        orchestratorService.handlePaymentEvent(event);
    }
}
