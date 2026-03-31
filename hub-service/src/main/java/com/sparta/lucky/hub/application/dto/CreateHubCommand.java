package com.sparta.lucky.hub.application.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateHubCommand {

    private final String name;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;

    private CreateHubCommand(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static CreateHubCommand of(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        return new CreateHubCommand(name, address, latitude, longitude);
    }
}