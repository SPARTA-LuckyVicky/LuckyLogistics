package com.sparta.lucky.deliveryservice.presentation.driver.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverUpdateCommand;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import java.util.UUID;

public record DeliveryDriverUpdateRequest(
    UUID hubId,
    DriverType type,
    DriverStatus status
) {

    public DeliveryDriverUpdateCommand toCommand() {
        return new DeliveryDriverUpdateCommand(hubId, type, status);
    }
}
