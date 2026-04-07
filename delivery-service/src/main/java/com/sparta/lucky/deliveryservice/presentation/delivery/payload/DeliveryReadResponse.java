package com.sparta.lucky.deliveryservice.presentation.delivery.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryReadResult;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DeliveryReadResponse(
    UUID id,
    UUID deliveryDriver,
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

    public static DeliveryReadResponse from(DeliveryReadResult result) {
        return DeliveryReadResponse.builder()
            .id(result.id()).
            deliveryDriver(result.companyDriverId())
            .orderId(result.orderId())
            .status(result.status())
            .originHub(result.originHub())
            .currentHub(result.currentHub())
            .destinationHub(result.destinationHub())
            .deliveryAddress(result.deliveryAddress())
            .recipientName(result.recipientName())
            .recipientSlackId(result.recipientSlackId())
            .startedAt(result.startedAt())
            .arrivedAt(result.arrivedAt())
            .build();
    }
}
