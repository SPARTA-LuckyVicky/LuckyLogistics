package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class OrderAlertRequest {
    private UUID orderId;
    private LocalDateTime orderedAt;
    private String productName;
    private Integer quantity;
    private String requestNote;
    private LocalDateTime requestedDeadline;
    private String recipientName;
    private String recipientSlackId;
    private String originHubName;
    private String destinationHubName;
    private String deliveryAddress;
    private String hubManagerSlackId;
    private Long totalDurationMinutes;
    private Long totalDistanceKm;
    private List<String> waypointNames;
}