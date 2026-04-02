package com.sparta.lucky.deliveryservice.presentation.driver.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverReadResult;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DeliveryDriverReadResponse (
    UUID driverId,
    UUID hubId,
    DriverType driverType,
    DriverStatus currentStatus
) {
    public static DeliveryDriverReadResponse fromResult(DeliveryDriverReadResult result) {
        return DeliveryDriverReadResponse.builder()
            .driverId(result.driverId())
            .hubId(result.hubId())
            .driverType(result.type())
            .currentStatus(result.status())
            .build();
    }
}
