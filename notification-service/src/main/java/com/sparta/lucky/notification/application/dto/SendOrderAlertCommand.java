package com.sparta.lucky.notification.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class SendOrderAlertCommand {

    @NotNull
    private UUID orderId;

    private Long totalDurationMinutes;
    private Long totalDistanceKm;
    private List<String> waypointNames;

}


