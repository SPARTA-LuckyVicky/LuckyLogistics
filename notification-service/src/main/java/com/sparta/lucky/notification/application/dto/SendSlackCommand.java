package com.sparta.lucky.notification.application.dto;

import com.sparta.lucky.notification.presentation.dto.PostSlackReqDto;
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

    public SendSlackCommand(String receiverSlackId, String messageContent,
                            UUID relatedOrderId, UUID senderId) {
        this.receiverSlackId = receiverSlackId;
        this.messageContent = messageContent;
        this.relatedOrderId = relatedOrderId;
        this.senderId = senderId;
    }
}
