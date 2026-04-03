package com.sparta.lucky.notification.infrastructure;

import com.sparta.lucky.notification.domain.MessageType;
import com.sparta.lucky.notification.domain.SlackMessage;
import com.sparta.lucky.notification.domain.SlackMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SlackMessageRepositoryImpl implements SlackMessageRepository {

    private final SlackMessageJpaRepository jpaRepository;

    @Override
    public SlackMessage saveAndFlush(SlackMessage slackMessage) {
        return jpaRepository.saveAndFlush(slackMessage);
    }

    @Override
    public Optional<SlackMessage> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<SlackMessage> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<SlackMessage> findByMessageType(MessageType messageType, Pageable pageable) {
        return jpaRepository.findByMessageType(messageType, pageable);
    }

    @Override
    public Page<SlackMessage> findByRelatedOrderId(UUID relatedOrderId, Pageable pageable) {
        return jpaRepository.findByRelatedOrderId(relatedOrderId, pageable);
    }
}