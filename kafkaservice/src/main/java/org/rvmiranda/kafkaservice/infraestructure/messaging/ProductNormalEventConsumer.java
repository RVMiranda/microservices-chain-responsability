package org.rvmiranda.kafkaservice.infraestructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.model.ProductEvent;
import org.rvmiranda.kafkaservice.entities.postgres.ProductHistory;
import org.rvmiranda.kafkaservice.repository.postgres.ProductHistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductNormalEventConsumer {

    private final ProductHistoryRepository productHistoryRepository;

    @KafkaListener(topics = "product-events", groupId = "product-normal-listener", containerFactory = "productEventKafkaListenerContainerFactory")
    public void consume(ProductEvent event) {
        log.info("Received Normal ProductEvent: {}", event);
        try {
            // Guardar en ProductHistory
            ProductHistory history = ProductHistory.builder()
                    .productId(event.getProductId() != null ? event.getProductId() : "N/A")
                    .action(event.getAction())
                    .details(event.getRequestData())
                    .createdAt(OffsetDateTime.now())
                    .build();
            productHistoryRepository.save(history);
            log.info("Saved ProductHistory for productId: {}", event.getProductId());

        } catch (Exception e) {
            log.error("Error processing Normal ProductEvent: {}", e.getMessage(), e);
        }
    }
}
