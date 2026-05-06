package org.rvmiranda.kafkaservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.model.OrderEvent;
import org.rvmiranda.kafkaservice.entities.postgres.OrderHistory;
import org.rvmiranda.kafkaservice.entities.postgres.Shipping;
import org.rvmiranda.kafkaservice.repository.postgres.OrderHistoryRepository;
import org.rvmiranda.kafkaservice.repository.postgres.ShippingRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderNormalEventConsumer {

    private final OrderHistoryRepository orderHistoryRepository;
    private final ShippingRepository shippingRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "order-normal-listener", containerFactory = "orderEventKafkaListenerContainerFactory")
    public void consume(OrderEvent event) {
        log.info("Received Normal OrderEvent: {}", event);
        try {
            // 1. Guardar en OrderHistory
            OrderHistory history = OrderHistory.builder()
                    .orderId(event.getOrderId() != null ? event.getOrderId() : "N/A")
                    .status(event.getStatus())
                    .details(event.getRequestData())
                    .createdAt(OffsetDateTime.now())
                    .build();
            orderHistoryRepository.save(history);
            log.info("Saved OrderHistory for orderId: {}", event.getOrderId());

            // 2. Chain of Responsibility: Enviar correo al cliente (simulado)
            log.info(">>> Enviando correo al cliente sobre actualización de orden: {}", event.getOrderId());

            // 3. Evaluar si debe guardarse en tabla de envíos (unificación de lógica)
            JsonNode requestDataNode = objectMapper.readTree(event.getRequestData());
            String status = requestDataNode.path("status").asText("");

            if ("PAGADA".equalsIgnoreCase(status) && "SUCCESS".equalsIgnoreCase(event.getStatus())) {
                Shipping shipping = shippingRepository.findByOrderId(event.getOrderId())
                        .orElse(Shipping.builder()
                                .orderId(event.getOrderId())
                                .createdAt(OffsetDateTime.now())
                                .build());
                shipping.setStatus("PENDING");
                shippingRepository.save(shipping);
                log.info("Saved/Updated Shipping table for orderId: {} due to status PAGADA", event.getOrderId());
            }

        } catch (Exception e) {
            log.error("Error processing Normal OrderEvent: {}", e.getMessage(), e);
        }
    }
}
