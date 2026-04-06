package com.sparta.lucky.deliveryservice.application.dto;

import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DeliveryDriverReadResult(
    UUID driverId,
    UUID hubId,
    DriverType type,
    DriverStatus status
) {

    public static DeliveryDriverReadResult from(DeliveryDriver driver) {
        return DeliveryDriverReadResult.builder()
            .driverId(driver.getUserId())
            .hubId(driver.getHubId())
            .type(driver.getType())
            .status(driver.getStatus())
            .build();
    }
}
