package com.sparta.lucky.hub.common.config;

import com.sparta.lucky.hub.common.filter.HeaderAuthenticationFilter;
import com.sparta.lucky.hub.common.filter.InternalRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/v3/api-docs/**",
//                                "/api-docs/**"
//                        ).permitAll()
//                        .anyRequest().authenticated()
                        // 현재는 모든 요청 permitAll
                        .anyRequest().permitAll()
                );
//                .addFilterBefore(new InternalRequestFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new HeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}