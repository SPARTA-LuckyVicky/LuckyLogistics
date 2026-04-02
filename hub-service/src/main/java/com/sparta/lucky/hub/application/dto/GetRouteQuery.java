package com.sparta.lucky.hub.application.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class GetRouteQuery {

    private final UUID originHubId;
    private final BigDecimal destinationLatitude;
    private final BigDecimal destinationLongitude;

    private GetRouteQuery(UUID originHubId, BigDecimal destinationLatitude, BigDecimal destinationLongitude) {
        this.originHubId = originHubId;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

    public static GetRouteQuery of(UUID originHubId, BigDecimal destinationLatitude, BigDecimal destinationLongitude) {
        return new GetRouteQuery(originHubId, destinationLatitude, destinationLongitude);
    }
}