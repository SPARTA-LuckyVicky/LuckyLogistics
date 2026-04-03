package com.sparta.lucky.notification.infrastructure;

import com.sparta.lucky.notification.domain.AiMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiMessageJpaRepository extends JpaRepository<AiMessage, UUID> {

    Page<AiMessage> findByRelatedOrderId(UUID relatedOrderId, Pageable pageable);

    Optional<AiMessage> findBySlackMessageId(UUID slackMessageId);
}