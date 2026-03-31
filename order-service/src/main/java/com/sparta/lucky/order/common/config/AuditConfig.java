package com.sparta.lucky.order.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // 나중에 JWT에서 userId or username 추출
        // 지금은 임시로 "system" 반환
        return Optional.of("system");
    }
}
