package com.sparta.lucky.deliveryservice.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

public record HubRouteResponse(
    List<RouteItem> route
) {
    public record RouteItem(
        UUID fromHubId,
        UUID toHubId,
        Long expectedDuration,
        Long expectedDistance
    ) {}
}
