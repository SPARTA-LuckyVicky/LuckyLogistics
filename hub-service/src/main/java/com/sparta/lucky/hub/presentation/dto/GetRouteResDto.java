package com.sparta.lucky.hub.presentation.dto;

import com.sparta.lucky.hub.application.dto.GetRouteResult;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class GetRouteResDto {

    private final UUID id;
    private final UUID originHubId;
    private final UUID destinationHubId;
    private final Integer duration;
    private final Integer distance;
    private final List<UUID> route;

    private GetRouteResDto(GetRouteResult result) {
        this.id = result.getId();
        this.originHubId = result.getOriginHubId();
        this.destinationHubId = result.getDestinationHubId();
        this.duration = result.getDuration();
        this.distance = result.getDistance();
        this.route = result.getRoute();
    }

    public static GetRouteResDto from(GetRouteResult result) {
        return new GetRouteResDto(result);
    }
}