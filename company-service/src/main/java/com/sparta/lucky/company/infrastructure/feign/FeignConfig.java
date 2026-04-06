package com.sparta.lucky.company.infrastructure.feign;

import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 클라이언트 공통 설정
 * - MicrometerObservationCapability: Feign 호출을 Micrometer Observation으로 래핑하여 Zipkin 트레이싱 연동
 */
@Configuration
public class FeignConfig {

    @Bean
    public MicrometerObservationCapability micrometerObservationCapability(ObservationRegistry registry) {
        return new MicrometerObservationCapability(registry);
    }
}