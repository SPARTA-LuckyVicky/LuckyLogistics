package com.sparta.lucky.notification.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SlackMessageRepository  extends JpaRepository<SlackMessage, UUID> {

    Page<SlackMessage> findByMessageType(MessageType messageType, Pageable pageable);

    Page<SlackMessage> findByRelatedOrderId(UUID relatedOrderId, Pageable pageable);
}
