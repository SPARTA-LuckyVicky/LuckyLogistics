package com.sparta.lucky.deliveryservice.infrastructure.client;

import com.sparta.lucky.deliveryservice.infrastructure.client.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="notification-service")
public interface NotificationClient {

    @PostMapping("/internal/api/v1/notifications/order-alert")
    void sendNotification( @RequestBody NotificationRequest notificationRequest );
}
