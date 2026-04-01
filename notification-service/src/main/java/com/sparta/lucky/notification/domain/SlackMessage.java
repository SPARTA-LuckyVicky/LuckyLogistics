package com.sparta.lucky.notification.domain;


import com.sparta.lucky.notification.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_slack_message", schema = "notification_schema")
@SQLRestriction("deleted_at IS NULL")
public class SlackMessage extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String receiverSlackId;

    @Column(nullable = false)
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    // 관련 주문 ID (주문 알림일 경우)
    @Column(columnDefinition = "uuid")
    private UUID relatedOrderId;

    // 발송자 (내부 시스템은 null 가능)
    @Column(columnDefinition = "uuid")
    private UUID senderId;

    public static SlackMessage create(
            String receiverSlackId,
            String messageContent,
            MessageType messageType,
            UUID relatedOrderId,
            UUID senderId
    ){
        SlackMessage msg = new SlackMessage();
        msg.receiverSlackId = receiverSlackId;
        msg.messageContent = messageContent;
        msg.messageType = messageType;
        msg.sentAt = LocalDateTime.now();
        msg.relatedOrderId = relatedOrderId;
        msg.senderId = senderId;
        return msg;
    }

    public void softDelete(UUID deletedBy) {
        super.softDelete(deletedBy);
    }




}
