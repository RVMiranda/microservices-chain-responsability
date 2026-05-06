package org.rvmiranda.ordenservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.ordenservice.application.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-events", groupId = "ordenservice-group")
    public void consumePaymentEvent(String message) {
        try {
            log.info("Evento de pago recibido en ordenservice: {}", message);
            JsonNode rootNode = objectMapper.readTree(message);
            
            String action = rootNode.path("action").asText();
            String status = rootNode.path("status").asText();
            
            if (!"SUCCESS".equals(status)) {
                return; // Solo nos interesan los procesados o reembolsados con éxito
            }

            JsonNode requestDataNode = objectMapper.readTree(rootNode.path("requestData").asText());
            String orderId = requestDataNode.path("orderId").asText();

            // Si es un REFUND, puede venir en un formato diferente si en el controlador no se mandó orderId
            // Revisar PaymentController.refundPayment: ahí mandamos orderId en requestDataJson
            
            if ("PROCESS_FULL".equals(action)) {
                log.info("Pago completo detectado. Actualizando orden {} a PAGADA", orderId);
                orderService.updateOrderStatus(orderId, "PAGADA");
                emitStatusChangeEvent(orderId, "PAGADA");
            } else if ("PROCESS_PARTIAL".equals(action)) {
                log.info("Pago parcial detectado. Actualizando orden {} a PAGO_PARCIAL", orderId);
                orderService.updateOrderStatus(orderId, "PAGO_PARCIAL");
                emitStatusChangeEvent(orderId, "PAGO_PARCIAL");
            } else if ("REFUND".equals(action)) {
                boolean isFullyPaid = requestDataNode.path("isFullyPaid").asBoolean(false);
                if (!isFullyPaid) {
                    log.info("Reembolso detectado. Actualizando orden {} a REEMBOLSADA", orderId);
                    orderService.updateOrderStatus(orderId, "REEMBOLSADA");
                    emitStatusChangeEvent(orderId, "REEMBOLSADA");
                }
            }
        } catch (Exception e) {
            log.error("Error procesando evento de pago en ordenservice: {}", e.getMessage(), e);
        }
    }

    private void emitStatusChangeEvent(String orderId, String newStatus) {
        String statusDataJson = String.format("{\"status\":\"%s\"}", newStatus);
        
        // El OrderEvent original asume esta estructura, lo simularemos aquí
        // usando un mapa genérico o un string si no tenemos la clase OrderEvent importada.
        // Dado que OrderEvent está en controller, es mejor enviar un JSON directamente 
        // o mapearlo a Map. Vamos a mapearlo a Map para asegurar que Jackson lo serialice bien,
        // o si es posible, podemos reusar la definición.
        
        java.util.Map<String, Object> statusEvent = new java.util.HashMap<>();
        statusEvent.put("eventId", UUID.randomUUID().toString());
        statusEvent.put("orderId", orderId);
        statusEvent.put("action", "UPDATE_ORDER_STATUS_FROM_PAYMENT");
        statusEvent.put("status", "SUCCESS");
        statusEvent.put("requestData", statusDataJson);
        statusEvent.put("responseData", null);
        statusEvent.put("errorDetails", null);

        kafkaTemplate.send("order-events", statusEvent);
        kafkaTemplate.send("order-status-changed-events", statusEvent);
    }
}
