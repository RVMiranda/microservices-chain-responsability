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
            // Si el evento viene de OrderController, la accion es CREATE_ORDER_INVENTORY
            String action = root.path("action").asText("");
            String status = root.path("status").asText("");
            
            if ("SUCCESS".equalsIgnoreCase(status) && "CREATE_ORDER_INVENTORY".equalsIgnoreCase(action)) {
                String requestDataStr = root.path("requestData").asText("{}");
                JsonNode requestData = objectMapper.readTree(requestDataStr);
                String productId = requestData.path("productId").asText("");
                int quantity = requestData.path("quantity").asInt(0);
                
                if (!productId.isEmpty() && quantity > 0) {
                    log.info("Reducing stock for product {} by {}", productId, quantity);
                    productService.reduceStock(productId, quantity);
                }
            }
        } catch (Exception e) {
            log.error("Error processing InventoryEvent: {}", e.getMessage(), e);
        }
    }
}
