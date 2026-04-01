package com.sparta.lucky.notification.application.dto;

import com.sparta.lucky.notification.presentation.dto.PostSlackReqDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class SendSlackCommand {

    private String receiverSlackId;
    private String messageContent;
    private UUID relatedOrderId;
    private UUID senderId;

    public static SendSlackCommand of(PostSlackReqDto dto, UUID senderId) {
        SendSlackCommand cmd = new SendSlackCommand();
        cmd.receiverSlackId = dto.getReceiverSlackId();
        cmd.messageContent = dto.getMessageContent();
        cmd.relatedOrderId = dto.getRelatedOrderId();
        cmd.senderId = senderId;
        return cmd;
    }
}
