package com.sparta.lucky.notification.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiMessageRepository {

    AiMessage save(AiMessage aiMessage);

    Optional<AiMessage> findById(UUID id);

    Page<AiMessage> findAll(Pageable pageable);

    Page<AiMessage> findByRelatedOrderId(UUID relatedOrderId, Pageable pageable);

    Optional<AiMessage> findBySlackMessageId(UUID slackMessageId);
}
