package com.sparta.lucky.notification.infrastructure;

import com.sparta.lucky.notification.domain.MessageType;
import com.sparta.lucky.notification.domain.SlackMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SlackMessageJpaRepository extends JpaRepository<SlackMessage, UUID> {

    Page<SlackMessage> findByMessageType(MessageType messageType, Pageable pageable);

    Page<SlackMessage> findByRelatedOrderId(UUID relatedOrderId, Pageable pageable);
}