package com.programming.delivery_service.service;

import com.programming.DeliveryEvent;
import com.programming.PaymentEvent;
import com.programming.delivery_service.entity.Delivery;
import com.programming.delivery_service.producer.DeliveryProducer;
import com.programming.delivery_service.repository.DeliveryRepository;

import com.programming.enums.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryProducer deliveryProducer;

    public void handlePayment(PaymentEvent paymentEvent) {
        Delivery delivery = Delivery.builder()
                .orderId(paymentEvent.getOrderId())
                .address("Warehouse Pickup - Auto Generated")
                .status(DeliveryStatus.PENDING)
                .build();

        Delivery saved = deliveryRepository.save(delivery);

        DeliveryEvent deliveryEvent = DeliveryEvent.builder()
                .orderId(saved.getOrderId())
                .deliveryId(saved.getId().toString())
                .status(DeliveryStatus.PENDING)
                .build();

        deliveryProducer.sendDeliveryEvent(deliveryEvent);
        log.info("[OrderID: {}] Delivery created and event published", saved.getOrderId());
    }


    public void markDeliveryFailed(String orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order " + orderId));

        delivery.setStatus(DeliveryStatus.FAILED);
        deliveryRepository.save(delivery);

        DeliveryEvent deliveryEvent = DeliveryEvent.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(delivery.getId().toString())
                .status(DeliveryStatus.FAILED) // <-- Enum directly
                .build();

        deliveryProducer.sendDeliveryEvent(deliveryEvent);
        log.info("Delivery FAILED and event published for order {}", orderId);
    }
}
