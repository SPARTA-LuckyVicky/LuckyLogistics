package com.sparta.lucky.notification.presentation.dto;

import com.sparta.lucky.notification.application.dto.SendSlackCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class PostSlackReqDto {

    @NotBlank(message = "수신자 슬랙 ID는 필수입니다.")
    private String receiverSlackId;

    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(max = 4000, message = "메시지 내용은 4000자를 초과할 수 없습니다.")
    private String messageContent;

    private UUID relatedOrderId;

    public SendSlackCommand toCommand(UUID senderId) {
        return new SendSlackCommand(
                this.receiverSlackId,
                this.messageContent,
                this.relatedOrderId,
                senderId
        );
    }

}
