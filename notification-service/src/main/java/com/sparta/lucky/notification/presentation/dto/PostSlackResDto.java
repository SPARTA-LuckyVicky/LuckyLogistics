package com.sparta.lucky.notification.presentation.dto;

import com.sparta.lucky.notification.application.dto.SlackMessageResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PostSlackResDto {

    private UUID id;
    private String receiverSlackId;
    private String messageContent;
    private LocalDateTime sentAt;

    public static PostSlackResDto from(SlackMessageResult result) {
        return PostSlackResDto.builder()
                .id(result.getId())
                .receiverSlackId(result.getReceiverSlackId())
                .messageContent(result.getMessageContent())
                .sentAt(result.getSentAt())
                .build();
    }

}
