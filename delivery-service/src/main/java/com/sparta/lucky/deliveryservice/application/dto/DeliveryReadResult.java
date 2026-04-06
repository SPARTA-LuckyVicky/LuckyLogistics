package com.sparta.lucky.deliveryservice.application.dto;

import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DeliveryReadResult(
    UUID id,
    UUID companyDriverId,
    UUID orderId,
    DeliveryStatus status,
    UUID originHub,
    UUID currentHub,
    UUID destinationHub,
    String deliveryAddress,
    String recipientName,
    String recipientSlackId,
    LocalDateTime startedAt,
    LocalDateTime arrivedAt
) {

    public static DeliveryReadResult from(Delivery delivery) {
        return DeliveryReadResult.builder()
            .id(delivery.getId())
            .companyDriverId(delivery.getDeliveryDriver().getId())
            .orderId(delivery.getOrderId())
            .currentHub(delivery.getCurrentHub())
            .status(delivery.getStatus())
            .originHub(delivery.getOriginHub())
            .destinationHub(delivery.getDestinationHub())
            .deliveryAddress(delivery.getDeliveryAddress())
            .recipientName(delivery.getRecipientName())
            .recipientSlackId(delivery.getRecipientSlackId())
            .startedAt(delivery.getStartedAt())
            .arrivedAt(delivery.getArrivedAt())
            .build();
    }

}
