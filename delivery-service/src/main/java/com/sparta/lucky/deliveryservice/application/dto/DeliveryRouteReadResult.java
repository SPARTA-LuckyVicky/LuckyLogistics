package com.sparta.lucky.deliveryservice.application.dto;

import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryRouteStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DeliveryRouteReadResult(
    UUID id,
    UUID driverId,
    Integer sequence,
    UUID fromHubId,
    UUID toHubId,
    DeliveryRouteStatus status,
    Long expectedDistance,
    Long expectedDurationSeconds,
    Long actualDistance,
    Long actualDurationSeconds,
    LocalDateTime startedAt,
    LocalDateTime arrivedAt
) {
    public static DeliveryRouteReadResult from(DeliveryRoute deliveryRoute) {
        return DeliveryRouteReadResult.builder()
            .id(deliveryRoute.getId())
            .driverId(deliveryRoute.getDeliveryDriver().getId())
            .sequence(deliveryRoute.getSequence())
            .fromHubId(deliveryRoute.getFromHubId())
            .toHubId(deliveryRoute.getToHubId())
            .status(deliveryRoute.getStatus())
            .expectedDistance(deliveryRoute.getExpectedDistance())
            .expectedDurationSeconds(deliveryRoute.getExpectedDurationSeconds())
            .actualDistance(deliveryRoute.getActualDistance())
            .actualDurationSeconds(deliveryRoute.getActualDurationSeconds())
            .startedAt(deliveryRoute.getStartedAt())
            .arrivedAt(deliveryRoute.getArrivedAt())
            .build();
    }
}
