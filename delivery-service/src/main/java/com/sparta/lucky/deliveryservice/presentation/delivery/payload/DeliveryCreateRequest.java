package com.sparta.lucky.deliveryservice.presentation.delivery.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryCreateRequest (

    @NotNull
    UUID orderId,

    @NotNull
    UUID companyId,

    @NotNull
    UUID originHubId,

    @NotBlank
    String recipientName,

    @NotBlank
    String recipientSlackId
) {
    public DeliveryCreateCommand toCommand() {
        return new DeliveryCreateCommand(
            orderId, companyId, originHubId, recipientName, recipientSlackId
        );
    }
}
