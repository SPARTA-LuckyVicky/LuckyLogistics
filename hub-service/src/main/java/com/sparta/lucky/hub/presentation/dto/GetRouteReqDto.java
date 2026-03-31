package com.sparta.lucky.hub.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class GetRouteReqDto {

    @NotNull
    private UUID startHubId;

    @NotNull
    private BigDecimal endLat;

    @NotNull
    private BigDecimal endLong;
}