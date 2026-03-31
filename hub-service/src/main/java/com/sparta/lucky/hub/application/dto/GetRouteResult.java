package com.sparta.lucky.hub.application.dto;

import com.sparta.lucky.hub.domain.HubRoute;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class GetRouteResult {

    private final UUID id;
    private final UUID originHubId;
    private final UUID destinationHubId;
    private final Integer duration;
    private final Integer distance;
    private final List<UUID> route;

    private GetRouteResult(HubRoute hubRoute, List<UUID> route) {
        this.id = hubRoute.getId();
        this.originHubId = hubRoute.getOriginHubId();
        this.destinationHubId = hubRoute.getDestinationHubId();
        this.duration = hubRoute.getDuration();
        this.distance = hubRoute.getDistance();
        this.route = route;
    }

    public static GetRouteResult of(HubRoute hubRoute, List<UUID> route) {
        return new GetRouteResult(hubRoute, route);
    }
}