package com.sparta.lucky.notification.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> {
            try {
                // Gateway가 주입한 X-User-Id 헤더에서 현재 요청자 UUID 추출
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs == null) return Optional.empty();

                String userId = attrs.getRequest().getHeader("X-User-Id");
                if (userId == null || userId.isBlank()) return Optional.empty();

                return Optional.of(UUID.fromString(userId));
            } catch (Exception e) {
                log.debug("X-User-Id 헤더에서 auditor 정보를 확인할 수 없습니다", e);
                return Optional.empty();
            }
        };
    }
}