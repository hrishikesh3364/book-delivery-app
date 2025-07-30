package com.programming.delivery_service.controller;

import com.programming.delivery_service.entity.Delivery;
import com.programming.delivery_service.repository.DeliveryRepository;
import com.programming.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryRepository.findAll());
    }

    @PostMapping("/{orderId}/fail")
    public ResponseEntity<String> markDeliveryFailed(@PathVariable String orderId) {
        deliveryService.markDeliveryFailed(orderId);
        return ResponseEntity.ok("Delivery marked as FAILED and refund triggered for order " + orderId);
    }
}
