package com.sparta.lucky.hub.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PostRouteReqDto {

    @NotNull
    private UUID originHubId;

    @NotNull
    private UUID destinationHubId;

    @NotNull
    @Min(0)
    private Integer distance;

    @NotNull
    @Min(0)
    private Integer duration;
}