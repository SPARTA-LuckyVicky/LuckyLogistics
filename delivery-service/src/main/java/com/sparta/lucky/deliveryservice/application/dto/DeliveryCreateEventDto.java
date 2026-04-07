package com.sparta.lucky.deliveryservice.application.dto;

import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DeliveryCreateEventDto(
    UUID id,
    UUID orderId,
    UUID originHub,
    UUID destinationHub,
    String recipientName,
    String recipientSlackId
) {
    public static DeliveryCreateEventDto from(Delivery delivery) {
        return DeliveryCreateEventDto.builder()
            .id(delivery.getId())
            .orderId(delivery.getOrderId())
            .originHub(delivery.getOriginHub())
            .destinationHub(delivery.getDestinationHub())
            .recipientName(delivery.getRecipientName())
            .recipientSlackId(delivery.getRecipientSlackId())
            .build();
    }
}
