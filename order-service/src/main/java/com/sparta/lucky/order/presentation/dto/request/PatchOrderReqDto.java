package com.sparta.lucky.order.presentation.dto.request;

import com.sparta.lucky.order.application.dto.request.UpdateOrderCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PatchOrderReqDto {

    private String requestNote;
    private LocalDateTime requestedDeadline;

    public UpdateOrderCommand toCommand() {
        return new UpdateOrderCommand(requestNote, requestedDeadline);
    }
}
