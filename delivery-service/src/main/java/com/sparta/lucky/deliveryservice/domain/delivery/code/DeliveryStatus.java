package com.sparta.lucky.deliveryservice.domain.delivery.code;

public enum DeliveryStatus {
    WAITING,
    HUB_TRANSITING,
    AT_DESTINATION_HUB,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED,
    CANCELLED
}
