package com.sparta.lucky.notification.presentation.dto;

import com.sparta.lucky.notification.application.dto.SendSlackCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class PostSlackReqDto {

    @NotBlank(message = "수신자 슬랙 ID는 필수입니다.")
    private String receiverSlackId;

    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String messageContent;

    private UUID relatedOrderId;

    public SendSlackCommand toCommand(UUID senderId) {
        return SendSlackCommand.of(this, senderId);
    }

}
