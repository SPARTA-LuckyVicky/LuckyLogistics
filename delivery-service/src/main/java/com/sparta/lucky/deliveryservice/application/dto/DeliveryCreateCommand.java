package com.sparta.lucky.deliveryservice.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryCreateCommand (
    UUID orderId,
    UUID companyId,
    UUID originHubId,
    String recipientName,
    String recipientSlackId,
    LocalDateTime delivery_due_date
) {

}
