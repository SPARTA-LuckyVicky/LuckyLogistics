package com.sparta.lucky.deliveryservice.presentation.driver.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverCreateCommand;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DeliveryDriverCreateRequest(

    @NotNull
    UUID driverId,

    @NotNull
    UUID hubId,

    @NotNull
    DriverType type
) {
    public DeliveryDriverCreateCommand toCommand() {
        return new DeliveryDriverCreateCommand(driverId, hubId, type);
    }
}
