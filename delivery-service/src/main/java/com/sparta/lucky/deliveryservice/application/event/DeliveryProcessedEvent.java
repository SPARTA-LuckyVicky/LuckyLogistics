package com.sparta.lucky.deliveryservice.application.event;

import java.util.UUID;

public record DeliveryProcessedEvent(
    UUID deliveryId
) {

}
