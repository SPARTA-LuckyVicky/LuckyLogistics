package com.sparta.lucky.notification.presentation.dto;

import com.sparta.lucky.notification.application.dto.SlackMessageResult;
import com.sparta.lucky.notification.domain.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetSlackResDto {

    private UUID id;
    private String receiverSlackId;
    private String messageContent;
    private MessageType messageType;
    private LocalDateTime sentAt;
    private UUID relatedOrderId;
    private LocalDateTime createdAt;

    public static GetSlackResDto from(SlackMessageResult result) {
        return GetSlackResDto.builder()
                .id(result.getId())
                .receiverSlackId(result.getReceiverSlackId())
                .messageContent(result.getMessageContent())
                .messageType(result.getMessageType())
                .sentAt(result.getSentAt())
                .relatedOrderId(result.getRelatedOrderId())
                .createdAt(result.getCreatedAt())
                .build();
    }
}