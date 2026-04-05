package com.sparta.lucky.deliveryservice.presentation.delivery.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryRouteReadResult;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryRouteStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DeliveryRouteReadResponse (
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
    public static DeliveryRouteReadResponse from(DeliveryRouteReadResult result) {
        return DeliveryRouteReadResponse.builder()
            .id(result.id())
            .driverId(result.driverId())
            .sequence(result.sequence())
            .fromHubId(result.fromHubId())
            .toHubId(result.toHubId())
            .status(result.status())
            .expectedDistance(result.expectedDistance())
            .expectedDurationSeconds(result.expectedDurationSeconds())
            .actualDistance(result.actualDistance())
            .actualDurationSeconds(result.actualDurationSeconds())
            .startedAt(result.startedAt())
            .arrivedAt(result.arrivedAt())
            .build();
    }
}
