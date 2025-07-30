package com.programming.orchestrator.controller;

import com.programming.OrderEvent;
import com.programming.orchestrator.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestrator")
@RequiredArgsConstructor
@Slf4j
public class OrchestratorController {

    private final OrchestratorService orchestratorService;

    @PostMapping("/orders")
    public ResponseEntity<String> startOrderSaga(@RequestBody OrderEvent orderEvent) {
        log.info("Received order via REST: {}", orderEvent);
        orchestratorService.handleOrder(orderEvent);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Saga started for orderId: " + orderEvent.getOrderId());
    }
}
