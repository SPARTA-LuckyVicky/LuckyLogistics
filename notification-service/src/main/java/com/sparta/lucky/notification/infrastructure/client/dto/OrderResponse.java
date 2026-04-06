package com.sparta.lucky.notification.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {

    private UUID id;
    private String productName;
    private Integer quantity;
    private String requestNote;
    private LocalDateTime requestedDeadline;
    private LocalDateTime createdAt;
    private String originHubName;
    private String destinationHubName;
    private String deliveryAddress;
    private String recipientName;
    private String recipientSlackId;
    private String hubManagerSlackId;

}
