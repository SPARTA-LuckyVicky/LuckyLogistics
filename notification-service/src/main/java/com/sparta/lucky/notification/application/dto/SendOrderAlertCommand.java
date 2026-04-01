package com.sparta.lucky.notification.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class SendOrderAlertCommand {

    private UUID orderId;
    private String requesterName;
    private String requesterEmail;
    private LocalDateTime orderedAt;
    private String productName;
    private Integer quantity;
    private String requestNote;
    private LocalDateTime requestedDeadline;

    private String originHubName;
    private List<String> waypoints;
    private String destinationHubName;
    private String deliveryAddress;

    private String recipientName;
    private String recipientSlackId;
    private String hubManagerSlackId;
    private String deliveryManagerName;
    private String deliveryManagerSlackId;
}


