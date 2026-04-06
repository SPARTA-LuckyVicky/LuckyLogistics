package com.sparta.lucky.hub.presentation.dto;

import com.sparta.lucky.hub.application.dto.GetRouteResult;
import com.sparta.lucky.hub.application.dto.GetRouteResult.RouteSegment;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class GetRouteResDto {

    private final UUID originHubId;
    private final UUID destinationHubId;
    private final Integer totalDuration;
    private final Integer totalDistance;
    private final List<RouteSegment> route;

    private GetRouteResDto(GetRouteResult result) {
        this.originHubId = result.getOriginHubId();
        this.destinationHubId = result.getDestinationHubId();
        this.totalDuration = result.getTotalDuration();
        this.totalDistance = result.getTotalDistance();
        this.route = result.getRoute();
    }

    public static GetRouteResDto from(GetRouteResult result) {
        return new GetRouteResDto(result);
    }
}