package com.sparta.lucky.hub.application.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class GetRouteQuery {

    private final UUID startHubId;
    private final BigDecimal endLatitude;
    private final BigDecimal endLongitude;

    private GetRouteQuery(UUID startHubId, BigDecimal endLatitude, BigDecimal endLongitude) {
        this.startHubId = startHubId;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    public static GetRouteQuery of(UUID startHubId, BigDecimal endLatitude, BigDecimal endLongitude) {
        return new GetRouteQuery(startHubId, endLatitude, endLongitude);
    }
}