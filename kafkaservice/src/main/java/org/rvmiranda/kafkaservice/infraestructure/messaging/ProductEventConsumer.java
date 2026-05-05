package org.rvmiranda.kafkaservice.infraestructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.application.service.RetryJobService;
import org.rvmiranda.kafkaservice.domain.model.ProductEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final RetryJobService retryJobService;

    @KafkaListener(topics = "product-events-retry", groupId = "product-events-retry-listener", containerFactory = "productEventKafkaListenerContainerFactory")
    public void consume(ProductEvent event) {
        log.info("Received ProductEvent: {}", event);
        if ("FAILED".equalsIgnoreCase(event.getStatus())) {
            log.warn("Detected FAILED event for product {}. Delegating to RetryJobService.", event.getProductId());
            retryJobService.processFailedEvent(event);
        } else {
            log.info("Event {} processed successfully without failure.", event.getEventId());
        }
    }
}
