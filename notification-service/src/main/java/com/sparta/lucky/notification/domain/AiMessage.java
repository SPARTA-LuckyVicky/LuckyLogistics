package com.sparta.lucky.notification.domain;

import com.sparta.lucky.notification.common.entity.BaseEntity;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_ai_message", schema = "notification_schema")
@SQLRestriction("deleted_at IS NULL")
public class AiMessage extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    // 관련 주문 ID
    @Column(columnDefinition = "uuid")
    private UUID relatedOrderId;

    // Gemini에 보낸 원본 요청 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String requestContent;

    // Gemini 응답 전체
    @Column(nullable = false, columnDefinition = "TEXT")
    private String responseContent;

    // 파싱된 최종 발송 시한 (슬랙 메시지에 포함되는 핵심 값)
    @Column(nullable = false)
    private String deadlineResult;

    // 이 AI 결과를 기반으로 발송된 슬랙 메시지 ID
    @Column(columnDefinition = "uuid")
    private UUID slackMessageId;

    public static AiMessage create(
            UUID relatedOrderId,
            String requestContent,
            String responseContent,
            String deadlineResult,
            UUID slackMessageId
    ) {
        AiMessage ai = new AiMessage();
        ai.relatedOrderId = relatedOrderId;
        ai.requestContent = requestContent;
        ai.responseContent = responseContent;
        ai.deadlineResult = deadlineResult;
        ai.slackMessageId = slackMessageId;
        return ai;
    }

    public void softDelete(UUID deletedBy) {
        super.softDelete(deletedBy);
    }

}
