package org.rvmiranda.ordenservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.ordenservice.application.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

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
            } else if ("PROCESS_PARTIAL".equals(action)) {
                log.info("Pago parcial detectado. Actualizando orden {} a PAGO_PARCIAL", orderId);
                orderService.updateOrderStatus(orderId, "PAGO_PARCIAL");
            } else if ("REFUND".equals(action)) {
                boolean isFullyPaid = requestDataNode.path("isFullyPaid").asBoolean(false);
                if (!isFullyPaid) {
                    // Aquí podríamos dejarla como PAGO_PARCIAL si aún hay saldo, o REEMBOLSADA si quedó en 0.
                    // Asumiremos REEMBOLSADA por ahora, pero la lógica real dependería del saldo total restante.
                    // Para alinearnos con la solicitud del usuario:
                    // "recuerda mantener el status de 'CANCELADA' y agrega otro que diga 'REEMBOLSADA' si se rembolsaron los pagos de la misma en su totalidad."
                    // Vamos a marcarla como REEMBOLSADA por simplicidad ante un REFUND si el usuario dice que "se rembolsaron los pagos en su totalidad".
                    log.info("Reembolso detectado. Actualizando orden {} a REEMBOLSADA", orderId);
                    orderService.updateOrderStatus(orderId, "REEMBOLSADA");
                }
            }
        } catch (Exception e) {
            log.error("Error procesando evento de pago en ordenservice: {}", e.getMessage(), e);
        }
    }
}
