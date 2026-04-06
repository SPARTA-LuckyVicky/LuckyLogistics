package com.sparta.lucky.order.infrastructure.client;

import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public MicrometerObservationCapability micrometerObservationCapability(
            ObservationRegistry registry) {
        return new MicrometerObservationCapability(registry);
    }
}
