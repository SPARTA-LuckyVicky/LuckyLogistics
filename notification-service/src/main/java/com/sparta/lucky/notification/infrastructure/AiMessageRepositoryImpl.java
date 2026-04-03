package com.sparta.lucky.notification.infrastructure;

import com.sparta.lucky.notification.domain.AiMessage;
import com.sparta.lucky.notification.domain.AiMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AiMessageRepositoryImpl implements AiMessageRepository {

    private final AiMessageJpaRepository jpaRepository;

    @Override
    public AiMessage save(AiMessage aiMessage) {
        return jpaRepository.save(aiMessage);
    }

    @Override
    public Optional<AiMessage> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<AiMessage> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<AiMessage> findByRelatedOrderId(UUID relatedOrderId, Pageable pageable) {
        return jpaRepository.findByRelatedOrderId(relatedOrderId, pageable);
    }

    @Override
    public Optional<AiMessage> findBySlackMessageId(UUID slackMessageId) {
        return jpaRepository.findBySlackMessageId(slackMessageId);
    }
}