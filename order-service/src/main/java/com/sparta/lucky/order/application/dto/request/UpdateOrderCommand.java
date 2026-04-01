package com.sparta.lucky.order.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UpdateOrderCommand {

    private final String requestNote;

    private final LocalDateTime requestedDeadline;
}
