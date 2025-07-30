package com.programming.orchestrator.service;

import com.programming.DeliveryEvent;
import com.programming.OrderEvent;
import com.programming.PaymentEvent;
import com.programming.enums.DeliveryStatus;
import com.programming.enums.OrderStatus;
import com.programming.enums.PaymentStatus;
import com.programming.orchestrator.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrchestratorService {

    private final EventProducer eventProducer;

    private static final String PAYMENT_TOPIC = "payment-events";
    private static final String DELIVERY_TOPIC = "delivery-events";
    private static final String ORDER_TOPIC = "order-events";

    // Track completed/cancelled orders to avoid infinite loops
    private final Set<String> completedOrders = ConcurrentHashMap.newKeySet();
    private final Random random = new Random();

    public void handleOrder(OrderEvent orderEvent) {
        if (completedOrders.contains(orderEvent.getOrderId())) {
            log.info("Order {} already completed/cancelled. Ignoring.", orderEvent.getOrderId());
            return;
        }

        // Avoid re-triggering saga for cancelled or completed
        if (orderEvent.getStatus() == OrderStatus.CANCELLED
                || orderEvent.getStatus() == OrderStatus.COMPLETED) {
            log.info("Order {} already in terminal state {}. Ignoring saga.",
                    orderEvent.getOrderId(), orderEvent.getStatus());
            completedOrders.add(orderEvent.getOrderId());
            return;
        }

        log.info("Saga started for order {}", orderEvent.getOrderId());

        // Auto simulate Payment Success/Failure
        PaymentStatus simulatedStatus = random.nextBoolean() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        log.info("Simulating Payment {} for order {}", simulatedStatus, orderEvent.getOrderId());

        PaymentEvent paymentEvent = PaymentEvent.builder()
                .orderId(orderEvent.getOrderId())
                .amount(orderEvent.getAmount())
                .paymentStatus(simulatedStatus)
                .build();

        eventProducer.sendEvent(PAYMENT_TOPIC, paymentEvent);
    }

    public void handlePaymentEvent(PaymentEvent event) {
        if (completedOrders.contains(event.getOrderId())) {
            log.info("Order {} already completed/cancelled. Ignoring Payment event.", event.getOrderId());
            return;
        }

        if (event.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment success, triggering delivery for {}", event.getOrderId());

            // Auto simulate Delivery Success/Failure
            DeliveryStatus simulatedDeliveryStatus = random.nextBoolean() ? DeliveryStatus.SUCCESS : DeliveryStatus.FAILED;
            log.info("Simulating Delivery {} for order {}", simulatedDeliveryStatus, event.getOrderId());

            DeliveryEvent deliveryEvent = DeliveryEvent.builder()
                    .orderId(event.getOrderId())
                    .status(simulatedDeliveryStatus)
                    .build();

            eventProducer.sendEvent(DELIVERY_TOPIC, deliveryEvent);

        } else if (event.getPaymentStatus() == PaymentStatus.REFUND) {
            log.info("Refund processed for {}", event.getOrderId());
            cancelOrder(event.getOrderId(), event.getAmount());
        } else {
            log.warn("Payment failed for {}, canceling order", event.getOrderId());
            cancelOrder(event.getOrderId(), event.getAmount());
        }
    }

    public void handleDeliveryEvent(DeliveryEvent event) {
        if (completedOrders.contains(event.getOrderId())) {
            log.info("Order {} already completed/cancelled. Ignoring Delivery event.", event.getOrderId());
            return;
        }

        if (event.getStatus() == DeliveryStatus.FAILED) {
            log.warn("Delivery failed, triggering refund for {}", event.getOrderId());

            double refundAmount = fetchOrderAmount(event.getOrderId());
            PaymentEvent refundEvent = PaymentEvent.builder()
                    .orderId(event.getOrderId())
                    .amount(refundAmount)
                    .paymentStatus(PaymentStatus.REFUND)
                    .build();

            eventProducer.sendEvent(PAYMENT_TOPIC, refundEvent);
        } else {
            log.info("Delivery completed successfully for {}", event.getOrderId());
            completeOrder(event.getOrderId());
        }
    }

    private void cancelOrder(String orderId, double amount) {
        log.info("Compensation: Canceling order {}", orderId);
        completedOrders.add(orderId);

        OrderEvent cancelEvent = OrderEvent.builder()
                .orderId(orderId)
                .amount(amount)
                .status(OrderStatus.CANCELLED)
                .build();

        eventProducer.sendEvent(ORDER_TOPIC, cancelEvent);
    }

    private void completeOrder(String orderId) {
        log.info("Marking order {} as COMPLETED", orderId);
        completedOrders.add(orderId);

        OrderEvent completeEvent = OrderEvent.builder()
                .orderId(orderId)
                .amount(fetchOrderAmount(orderId))
                .status(OrderStatus.COMPLETED)
                .build();

        eventProducer.sendEvent(ORDER_TOPIC, completeEvent);
    }

    private double fetchOrderAmount(String orderId) {
        return 100.0; // for demo
    }
}
