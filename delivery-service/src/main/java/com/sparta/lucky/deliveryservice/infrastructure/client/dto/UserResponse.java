package com.sparta.lucky.deliveryservice.infrastructure.client.dto;

import java.util.UUID;

public record UserResponse(
    UUID hubId,
    String slackId
) {

}
