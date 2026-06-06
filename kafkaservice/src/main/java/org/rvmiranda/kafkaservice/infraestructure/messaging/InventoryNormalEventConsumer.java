package org.rvmiranda.kafkaservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.entities.postgres.ProductHistory;
import org.rvmiranda.kafkaservice.repository.postgres.ProductHistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryNormalEventConsumer {

    private final ProductHistoryRepository productHistoryRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory_update_events", groupId = "inventory-normal-listener", containerFactory = "retryKafkaListenerContainerFactory")
    public void consume(String message) {
        log.info("Received Normal InventoryEvent: {}", message);
        try {
            JsonNode root = objectMapper.readTree(message);
            String action = root.path("action").asText("");
            
            // Tratamos de leer productId del requestData (enviado por ProductController u OrderController)
            JsonNode requestData = root.path("requestData");
            if (requestData.isTextual()) {
                requestData = objectMapper.readTree(requestData.asText());
            }
            
            String productId = requestData.path("productId").asText("");
            if (productId.isEmpty()) {
                // Si no está en requestData, intentamos ver si vino en el payload principal (en caso de OrderEvent/ProductEvent mal seteados)
                productId = root.path("productId").asText("");
            }
            if (productId.isEmpty()) {
                productId = root.path("orderId").asText("N/A"); // Fallback
            }

            ProductHistory history = ProductHistory.builder()
                    .productId(productId)
                    .action(action)
                    .details(root.path("requestData").asText("{}"))
                    .createdAt(OffsetDateTime.now())
                    .build();
            productHistoryRepository.save(history);
            log.info("Saved ProductHistory for action: {}", action);

        } catch (Exception e) {
            log.error("Error processing Normal InventoryEvent: {}", e.getMessage(), e);
        }
    }
}
