package com.programming.order_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsResponse {
    private Long orderId;
    private List<String> bookIds;
    private double amount;
    private String status;
}
