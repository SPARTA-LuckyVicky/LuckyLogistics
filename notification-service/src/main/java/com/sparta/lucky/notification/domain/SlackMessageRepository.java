package com.sparta.lucky.notification.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SlackMessageRepository {

    SlackMessage save(SlackMessage slackMessage);

    Optional<SlackMessage> findById(UUID id);

    Page<SlackMessage> findAll(Pageable pageable);

    Page<SlackMessage> findByMessageType(MessageType messageType, Pageable pageable);

    Page<SlackMessage> findByRelatedOrderId(UUID relatedOrderId, Pageable pageable);
}
