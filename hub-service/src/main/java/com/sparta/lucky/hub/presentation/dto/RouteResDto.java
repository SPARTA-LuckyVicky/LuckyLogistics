package com.sparta.lucky.hub.presentation.dto;

import com.sparta.lucky.hub.domain.HubRoute;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RouteResDto {

    private final UUID id;
    private final UUID originHubId;
    private final UUID destinationHubId;
    private final Integer distance;
    private final Integer duration;

    private RouteResDto(HubRoute route) {
        this.id = route.getId();
        this.originHubId = route.getOriginHubId();
        this.destinationHubId = route.getDestinationHubId();
        this.distance = route.getDistance();
        this.duration = route.getDuration();
    }

    public static RouteResDto from(HubRoute route) {
        return new RouteResDto(route);
    }
}