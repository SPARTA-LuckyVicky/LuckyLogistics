package com.sparta.lucky.order.application.dto;

import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrderInternalResponse {

    private UUID id;
    private UUID requesterCompanyId;
    private UUID receiverCompanyId;
    private UUID productId;
    private String productName;
    private Integer quantity;
    private Integer unitPrice;
    private Integer totalPrice;
    private UUID deliveryId;
    private String originHubName;
    private String destinationHubName;
    private String deliveryAddress;
    private String recipientName;
    private String recipientSlackId;
    private String hubManagerSlackId;
    private String requestNote;
    private LocalDateTime requestedDeadline;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private UUID createdBy;

    public static OrderInternalResponse from(Order order) {
        return OrderInternalResponse.builder()
                .id(order.getId())
                .requesterCompanyId(order.getRequesterCompanyId())
                .receiverCompanyId(order.getReceiverCompanyId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalPrice(order.getTotalPrice())
                .deliveryId(order.getDeliveryId())
                .originHubName(order.getOriginHubName())
                .destinationHubName(order.getDestinationHubName())
                .deliveryAddress(order.getDeliveryAddress())
                .recipientName(order.getRecipientName())
                .recipientSlackId(order.getRecipientSlackId())
                .hubManagerSlackId(order.getHubManagerSlackId())
                .requestNote(order.getRequestNote())
                .requestedDeadline(order.getRequestedDeadline())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .createdBy(order.getCreatedBy())
                .build();
    }

}
