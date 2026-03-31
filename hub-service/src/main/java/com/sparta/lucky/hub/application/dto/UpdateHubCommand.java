package com.sparta.lucky.hub.application.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class UpdateHubCommand {

    private final UUID hubId;
    private final String name;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;

    private UpdateHubCommand(UUID hubId, String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.hubId = hubId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static UpdateHubCommand of(UUID hubId, String name, String address, BigDecimal latitude, BigDecimal longitude) {
        return new UpdateHubCommand(hubId, name, address, latitude, longitude);
    }
}