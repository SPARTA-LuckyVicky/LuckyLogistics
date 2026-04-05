package com.sparta.lucky.deliveryservice.application.event;

import com.sparta.lucky.deliveryservice.application.service.DeliveryRouteReadService;
import com.sparta.lucky.deliveryservice.application.dto.DeliveryReadResult;
import com.sparta.lucky.deliveryservice.application.dto.DeliveryRouteReadResult;
import com.sparta.lucky.deliveryservice.application.policy.DeliveryReadService;
import com.sparta.lucky.deliveryservice.infrastructure.client.NotificationClient;
import com.sparta.lucky.deliveryservice.infrastructure.client.dto.NotificationRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryProcessedEventListener {

    private final DeliveryReadService deliveryReadService;
    private final DeliveryRouteReadService deliveryRouteReadService;
    private final NotificationClient notificationClient;

    @Async("deliveryAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(DeliveryProcessedEvent event) {
        UUID deliveryId = event.deliveryId();

        DeliveryReadResult delivery = deliveryReadService.getDelivery(deliveryId);

        List<DeliveryRouteReadResult> routeList = delivery.originHub().equals(delivery.destinationHub())
            ? List.of()
            : deliveryRouteReadService.getDeliveryRoutes(delivery.id());

        notificationClient.sendNotification(NotificationRequest.from(delivery, routeList));
    }
}
