package com.sparta.lucky.deliveryservice.application.event;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryCreateEventDto;

public record DeliveryCreateEvent (
    DeliveryCreateEventDto dto
){

}
