package com.sparta.lucky.deliveryservice.infrastructure.client;

import com.sparta.lucky.deliveryservice.infrastructure.client.dto.UserHubResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/internal/v1/users/{userId}")
    UserHubResponse getUserHubId(@PathVariable UUID userId);
}
