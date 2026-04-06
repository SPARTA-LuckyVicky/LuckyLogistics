package com.sparta.lucky.deliveryservice.infrastructure.client.dto;

import java.util.UUID;

public record CompanyResponse(
    UUID hubId,
    String address
) {

}
