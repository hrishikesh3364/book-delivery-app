package com.programming.order_service.service;

import com.programming.OrderEvent;
import com.programming.enums.OrderStatus;
import com.programming.order_service.dto.OrderDetailsResponse;
import com.programming.order_service.dto.OrderRequest;
import com.programming.order_service.dto.OrderResponse;
import com.programming.order_service.entity.Order;
import com.programming.order_service.exception.OrderNotFoundException;
import com.programming.order_service.kafkaProducer.OrderProducer;
import com.programming.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final ModelMapper modelMapper;

    public OrderResponse createOrder(OrderRequest request) {
        // Map request -> entity
        Order order = modelMapper.map(request, Order.class);
        order.setBookIds(String.join(",", request.getBookIds()));
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // Build OrderEvent with ENUM
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(savedOrder.getId().toString())
                .amount(savedOrder.getAmount())
                .status(OrderStatus.PENDING)  // use generated method
                .build();

        // Publish event
        orderProducer.sendOrderEvent(orderEvent);
        log.info("Order created and event published: {}", orderEvent);

        return new OrderResponse(savedOrder.getId(), OrderStatus.PENDING.name());
    }

    public OrderDetailsResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        return OrderDetailsResponse.builder()
                .orderId(order.getId())
                .bookIds(Arrays.asList(order.getBookIds().split(",")))
                .amount(order.getAmount())
                .status(order.getStatus().name())  // Convert Enum -> String for DTO
                .build();
    }

    public List<OrderDetailsResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> OrderDetailsResponse.builder()
                        .orderId(order.getId())
                        .bookIds(Arrays.asList(order.getBookIds().split(",")))
                        .amount(order.getAmount())
                        .status(order.getStatus().name())  // Convert Enum -> String
                        .build())
                .collect(Collectors.toList());
    }
}
