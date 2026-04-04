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
    private UUID originHubId;  // from_hub_id → origin_hub_id로 변경
    private String recipientName;
    private String recipientSlackId;
    private LocalDateTime deliveryDueDate;
}