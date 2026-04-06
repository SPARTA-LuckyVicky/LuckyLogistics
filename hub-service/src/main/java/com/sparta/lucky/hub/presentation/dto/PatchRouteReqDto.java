package com.sparta.lucky.hub.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PatchRouteReqDto {

    @NotNull
    @Min(0)
    private Integer distance;

    @NotNull
    @Min(0)
    private Integer duration;
}