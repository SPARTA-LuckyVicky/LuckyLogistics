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
    String recipientSlackId,

    /* Note: Would it be better to use OffsetDateTime? LocalDateTime means local time of the server.
    *   Also, deliveryDueDate is not required field for creating delivery data.
    *   I think it would be better to inform notification-service that delivery has been created.
    *   And then, let notification-service send request to order-service for info like due-date.
    * */
    @NotNull
    LocalDateTime deliveryDueDate
) {
    public DeliveryCreateCommand toCommand() {
        return new DeliveryCreateCommand(
            orderId, companyId, originHubId, recipientName, recipientSlackId, deliveryDueDate
        );
    }
}
