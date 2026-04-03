package com.sparta.lucky.notification.application.dto;

import com.sparta.lucky.notification.domain.AiMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AiMessageResult {

    private UUID id;
    private UUID relatedOrderId;
    private String requestContent;
    private String responseContent;
    private String deadlineResult;
    private UUID slackMessageId;
    private LocalDateTime createdAt;

    public static AiMessageResult from(AiMessage ai) {
        return AiMessageResult.builder()
                .id(ai.getId())
                .relatedOrderId(ai.getRelatedOrderId())
                .requestContent(ai.getRequestContent())
                .responseContent(ai.getResponseContent())
                .deadlineResult(ai.getDeadlineResult())
                .slackMessageId(ai.getSlackMessageId())
                .createdAt(ai.getCreatedAt())
                .build();
    }

}
