package com.sparta.lucky.order.presentation.dto;

import com.sparta.lucky.order.application.dto.UpdateOrderCommand;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PatchOrderReqDto {

    private String requestNote;

    @Future(message = "납기일은 현재 이후여야 합니다.")
    private LocalDateTime requestedDeadline;

    public UpdateOrderCommand toCommand() {
        return new UpdateOrderCommand(requestNote, requestedDeadline);
    }
}
