package org.rvmiranda.kafkaservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.model.PaymentEvent;
import org.rvmiranda.kafkaservice.entities.postgres.PaymentHistory;
import org.rvmiranda.kafkaservice.entities.postgres.Shipping;
import org.rvmiranda.kafkaservice.repository.postgres.PaymentHistoryRepository;
import org.rvmiranda.kafkaservice.repository.postgres.ShippingRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentNormalEventConsumer {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ShippingRepository shippingRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment_received_events", groupId = "payment-normal-listener", containerFactory = "paymentEventKafkaListenerContainerFactory")
    public void consume(PaymentEvent event) {
        log.info("Received Normal PaymentEvent: {}", event);
        try {
            // 1. Guardar en PaymentHistory
            PaymentHistory history = PaymentHistory.builder()
                    .paymentId(event.getPaymentId() != null ? event.getPaymentId() : "N/A")
                    .status(event.getStatus())
                    .details(event.getRequestData())
                    .createdAt(OffsetDateTime.now())
                    .build();
            paymentHistoryRepository.save(history);
            log.info("Saved PaymentHistory for paymentId: {}", event.getPaymentId());

            // 2. Chain of Responsibility: Enviar correo (simulado)
            log.info(">>> Enviando correo de pago recibido para el pago: {}", event.getPaymentId());

            // 3. Evaluar si debe guardarse en la tabla de envíos (shipping)
            if ("SUCCESS".equalsIgnoreCase(event.getStatus()) && "PROCESS".equalsIgnoreCase(event.getAction())) {
                JsonNode requestDataNode = objectMapper.readTree(event.getRequestData());
                String orderId = requestDataNode.path("orderId").asText("");
                if (!orderId.isEmpty()) {
                    Shipping shipping = shippingRepository.findByOrderId(orderId)
                            .orElse(Shipping.builder()
                                    .orderId(orderId)
                                    .createdAt(OffsetDateTime.now())
                                    .build());
                    shipping.setStatus("PENDING");
                    shippingRepository.save(shipping);
                    log.info("Saved/Updated Shipping table for orderId: {}", orderId);
                }
            }

        } catch (Exception e) {
            log.error("Error processing Normal PaymentEvent: {}", e.getMessage(), e);
        }
    }
}
