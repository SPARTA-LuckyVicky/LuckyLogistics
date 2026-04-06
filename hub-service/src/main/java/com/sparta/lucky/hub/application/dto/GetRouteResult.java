package com.sparta.lucky.hub.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class GetRouteResult {

    private final UUID originHubId;
    private final UUID destinationHubId;
    private final Integer totalDuration;
    private final Integer totalDistance;
    private final List<RouteSegment> route;

    public record RouteSegment(
            @JsonProperty("fromHubId") UUID fromHubId,
            @JsonProperty("toHubId") UUID toHubId,
            @JsonProperty("expectedDuration") Integer expectedDuration,
            @JsonProperty("expectedDistance") Integer expectedDistance
    ) {}

    @JsonCreator
    private GetRouteResult(
            @JsonProperty("originHubId") UUID originHubId,
            @JsonProperty("destinationHubId") UUID destinationHubId,
            @JsonProperty("totalDuration") Integer totalDuration,
            @JsonProperty("totalDistance") Integer totalDistance,
            @JsonProperty("route") List<RouteSegment> route
    ) {
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.route = route;
    }

    public static GetRouteResult of(UUID originHubId, UUID destinationHubId, Integer totalDuration, Integer totalDistance, List<RouteSegment> route) {
        return new GetRouteResult(originHubId, destinationHubId, totalDuration, totalDistance, route);
    }
}