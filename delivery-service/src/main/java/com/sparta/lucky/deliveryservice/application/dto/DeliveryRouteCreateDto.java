package com.sparta.lucky.deliveryservice.application.dto;

import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.infrastructure.client.dto.HubRouteResponse.RouteItem;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DeliveryRouteCreateDto(
    Delivery delivery,
    DeliveryDriver driver,
    Integer sequence,
    UUID fromHubId,
    UUID toHubId,
    Long expectedDistance,
    Long expectedDurationSeconds
) {
    public static DeliveryRouteCreateDto from(RouteItem res, Delivery delivery, DeliveryDriver deliveryDriver, Integer sequence) {
        return DeliveryRouteCreateDto.builder()
            .delivery(delivery)
            .driver(deliveryDriver)
            .sequence(sequence)
            .fromHubId(res.fromHubId())
            .toHubId(res.toHubId())
            .expectedDistance(res.expectedDistance())
            .expectedDurationSeconds(res.expectedDuration())
            .build();
    }
}
