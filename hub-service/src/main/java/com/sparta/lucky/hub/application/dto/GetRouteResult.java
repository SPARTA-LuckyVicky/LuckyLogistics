package com.sparta.lucky.hub.application.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class GetRouteResult {

    private final UUID originHubId;
    private final UUID destinationHubId;
    private final Integer totalDuration;
    private final Integer totalDistance;
    private final List<UUID> route;

    private GetRouteResult(UUID originHubId, UUID destinationHubId, Integer totalDuration, Integer totalDistance, List<UUID> route) {
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.route = route;
    }

    public static GetRouteResult of(UUID originHubId, UUID destinationHubId, Integer totalDuration, Integer totalDistance, List<UUID> route) {
        return new GetRouteResult(originHubId, destinationHubId, totalDuration, totalDistance, route);
    }
}