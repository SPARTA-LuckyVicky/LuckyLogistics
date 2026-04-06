package com.sparta.lucky.company.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 내부 API — 서비스 간 직접 호출, JWT 없음. 컨트롤러에서 X-Internal-Request 헤더로 검증
                        .requestMatchers("/internal/api/v1/**").permitAll()
                        // Swagger UI
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                        // 외부 API — Gateway가 JWT 검증 후 X-User-Id, X-User-Role 헤더 주입
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
