package com.sparta.lucky.deliveryservice.common.security;

import com.sparta.lucky.deliveryservice.common.code.Role;
import com.sparta.lucky.deliveryservice.common.security.filter.ExternalAuthenticationFilter;
import com.sparta.lucky.deliveryservice.common.security.filter.InternalAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
        InternalAuthenticationFilter internalAuthenticationFilter,
        ExternalAuthenticationFilter externalAuthenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/internal/**").hasRole("INTERNAL")
                    .requestMatchers("/api/v1/drivers/me").hasRole(Role.DELIVERY_DRIVER.toString())
                    .requestMatchers("/api/v1/drivers/**").hasAnyRole(Role.MASTER.toString(), Role.HUB_MANAGER.toString())
                    .requestMatchers(HttpMethod.POST, "/api/v1/deliveries").hasRole(Role.MASTER.toString())
                // permit all while dev
                    .anyRequest().permitAll()
//                .anyRequest().authenticated()
            )
            .addFilterBefore(internalAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(externalAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
