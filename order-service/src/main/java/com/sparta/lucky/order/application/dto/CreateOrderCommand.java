package com.sparta.lucky.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateOrderCommand {

    private UUID receiverCompanyId;

    private UUID productId;

    private Integer quantity;

    private String requestNote;

    private LocalDateTime requestedDeadline;

}
