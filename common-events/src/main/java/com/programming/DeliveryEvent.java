package com.programming;

import com.programming.enums.DeliveryStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryEvent {
    private String orderId;
    private String deliveryId;
    private DeliveryStatus status;// enum
}
