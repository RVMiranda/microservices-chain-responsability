package org.rvmiranda.productservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.productservice.application.service.ProductService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory_update_events", groupId = "productservice-inventory-group")
    public void consume(String message) {
        log.info("Received InventoryEvent: {}", message);
        try {
            JsonNode root = objectMapper.readTree(message);
            
            // Verificamos si el evento viene con estructura de OrderEvent
            String action = root.path("action").asText("");
            String status = root.path("status").asText("");
            
            if ("SUCCESS".equalsIgnoreCase(status) && 
                ("CREATE_ORDER_INVENTORY".equalsIgnoreCase(action) || "RESTORE_ORDER_INVENTORY".equalsIgnoreCase(action))) {
                
                // requestData puede venir como un string JSON serializado o como un objeto
                JsonNode requestData = root.path("requestData");
                if (requestData.isTextual()) {
                    requestData = objectMapper.readTree(requestData.asText());
                }
                
                String productId = requestData.path("productId").asText("");
                int quantity = requestData.path("quantity").asInt(0);
                
                if (!productId.isEmpty() && quantity > 0) {
                    if ("CREATE_ORDER_INVENTORY".equalsIgnoreCase(action)) {
                        log.info("Reducing stock for product {} by {}", productId, quantity);
                        productService.reduceStock(productId, quantity);
                    } else if ("RESTORE_ORDER_INVENTORY".equalsIgnoreCase(action)) {
                        log.info("Restoring stock for product {} by {}", productId, quantity);
                        productService.restoreStock(productId, quantity);
                    }
                } else {
                    log.warn("Could not extract productId or quantity from event data. ProductId: {}, Quantity: {}", productId, quantity);
                }
            } else {
                log.info("Event ignored. Status: {}, Action: {}", status, action);
            }
        } catch (Exception e) {
            log.error("Error processing InventoryEvent: {}", e.getMessage(), e);
        }
    }
}
