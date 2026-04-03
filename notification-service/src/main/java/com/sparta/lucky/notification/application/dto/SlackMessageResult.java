package com.sparta.lucky.notification.application.dto;

import com.sparta.lucky.notification.domain.MessageType;
import com.sparta.lucky.notification.domain.SlackMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class SlackMessageResult {

    private UUID id;
    private String receiverSlackId;
    private String messageContent;
    private MessageType messageType;
    private LocalDateTime sentAt;
    private UUID relatedOrderId;
    private UUID senderId;
    private LocalDateTime createdAt;

    public static SlackMessageResult from(SlackMessage msg) {
        return SlackMessageResult.builder()
                .id(msg.getId())
                .receiverSlackId(msg.getReceiverSlackId())
                .messageContent(msg.getMessageContent())
                .messageType(msg.getMessageType())
                .sentAt(msg.getSentAt())
                .relatedOrderId(msg.getRelatedOrderId())
                .senderId(msg.getSenderId())
                .createdAt(msg.getCreatedAt())
                .build();
    }

}
