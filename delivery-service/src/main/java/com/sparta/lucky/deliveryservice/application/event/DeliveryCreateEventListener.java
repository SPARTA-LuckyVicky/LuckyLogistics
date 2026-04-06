package com.sparta.lucky.deliveryservice.application.event;

import com.sparta.lucky.deliveryservice.application.DeliveryProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCreateEventListener {

    private final DeliveryProcessingService deliveryProcessingService;

    @Async("deliveryAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(DeliveryCreateEvent event) {
        log.info("[INFO] DeliveryCreateEvent :: deliveryId={}", event.dto().id());
        deliveryProcessingService.process(event.dto().id());
    }
}
