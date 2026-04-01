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
    private Long totalDurationMinutes;  // 허브 루트 총 소요시간
    private Long totalDistanceKm;       // 총 거리
    private List<String> waypointNames; // 경유지 허브명 (UUID → 이름 변환 후)

    private String hubManagerSlackId;


}


