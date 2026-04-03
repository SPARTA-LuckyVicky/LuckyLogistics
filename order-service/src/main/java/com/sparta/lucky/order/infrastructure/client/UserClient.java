package com.sparta.lucky.order.infrastructure.client;

import com.sparta.lucky.order.infrastructure.client.dto.FeignApiResponse;
import com.sparta.lucky.order.infrastructure.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/internal/api/v1/users/{userId}")
    FeignApiResponse<UserResponse> getUser(@PathVariable UUID userId);
}