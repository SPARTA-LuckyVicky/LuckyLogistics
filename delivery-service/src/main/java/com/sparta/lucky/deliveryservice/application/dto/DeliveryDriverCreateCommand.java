package com.sparta.lucky.deliveryservice.application.dto;

import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import java.util.UUID;

public record DeliveryDriverCreateCommand(
    UUID driverId,
    UUID hubId,
    DriverType type
) {

}
