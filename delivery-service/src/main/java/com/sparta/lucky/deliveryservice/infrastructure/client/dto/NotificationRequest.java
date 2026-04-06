package com.sparta.lucky.deliveryservice.infrastructure.client.dto;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryReadResult;
import com.sparta.lucky.deliveryservice.application.dto.DeliveryRouteReadResult;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationRequest(
    UUID deliveryId,
    UUID orderId,
    Long expectedTotalDistanceMeter,
    Long expectedTotalDurationSeconds,
    List<UUID> routes
) {
    public static NotificationRequest from(
        DeliveryReadResult delivery,
        List<DeliveryRouteReadResult> route
    ) {
        long totalDistanceMeter = route.stream()
            .mapToLong(DeliveryRouteReadResult::expectedDistance)
            .sum();

        long totalDurationSeconds = route.stream()
            .mapToLong(DeliveryRouteReadResult::expectedDurationSeconds)
            .sum();

        List<UUID> routeIds = new ArrayList<>();
        routeIds.add(delivery.originHub());
        route.stream().map(DeliveryRouteReadResult::toHubId).forEach(routeIds::add);

        return NotificationRequest.builder()
            .deliveryId(delivery.id())
            .orderId(delivery.orderId())
            .expectedTotalDistanceMeter(totalDistanceMeter)
            .expectedTotalDurationSeconds(totalDurationSeconds)
            .routes(routeIds)
            .build();
    }
}
