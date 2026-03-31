package com.sparta.lucky.hub.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PatchHubManagerReqDto {

    @NotNull
    private UUID managerId;
}