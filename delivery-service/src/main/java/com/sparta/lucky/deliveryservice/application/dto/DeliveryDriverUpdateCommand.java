package com.sparta.lucky.deliveryservice.application.dto;

import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import java.util.UUID;

public record DeliveryDriverUpdateCommand(
    UUID hubId,
    DriverType type,
    DriverStatus status
) {

}
