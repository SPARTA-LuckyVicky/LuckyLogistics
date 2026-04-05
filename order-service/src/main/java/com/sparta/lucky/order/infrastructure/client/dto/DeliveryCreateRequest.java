package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeliveryCreateRequest {
    private UUID orderId;
    private UUID companyId;
    private UUID originHubId;
    private String recipientName;
    private String recipientSlackId;
}