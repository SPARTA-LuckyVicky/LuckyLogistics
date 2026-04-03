package com.sparta.lucky.deliveryservice.common.config;

import com.sparta.lucky.deliveryservice.common.code.Role;
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
                    .requestMatchers("/api/v1/drivers/me").hasRole(Role.DELIVERY_DRIVER.toString())
                    .requestMatchers("/api/v1/drivers/**").hasAnyRole(Role.MASTER.toString(), Role.HUB_MANAGER.toString())
                // permit all while dev
                    .anyRequest().permitAll()
//                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
