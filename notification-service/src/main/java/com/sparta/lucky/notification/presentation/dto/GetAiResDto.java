package com.sparta.lucky.notification.presentation.dto;

import com.sparta.lucky.notification.application.dto.AiMessageResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetAiResDto {

    private UUID id;
    private UUID relatedOrderId;
    private String deadlineResult;
    private UUID slackMessageId;
    private LocalDateTime createdAt;

    public static GetAiResDto from(AiMessageResult result) {
        return GetAiResDto.builder()
                .id(result.getId())
                .relatedOrderId(result.getRelatedOrderId())
                .deadlineResult(result.getDeadlineResult())
                .slackMessageId(result.getSlackMessageId())
                .createdAt(result.getCreatedAt())
                .build();
    }
}