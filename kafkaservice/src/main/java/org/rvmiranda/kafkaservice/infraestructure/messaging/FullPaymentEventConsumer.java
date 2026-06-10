package org.rvmiranda.kafkaservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.model.PaymentEvent;
import org.rvmiranda.kafkaservice.entities.postgres.Shipping;
import org.rvmiranda.kafkaservice.repository.postgres.ShippingRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FullPaymentEventConsumer {

    private final ShippingRepository shippingRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "full_recieved_payments_events", groupId = "full-payment-listener", containerFactory = "paymentEventKafkaListenerContainerFactory")
    public void consume(PaymentEvent event) {
        log.info("Received Full PaymentEvent: {}", event);
        try {
            if ("SUCCESS".equalsIgnoreCase(event.getStatus())) {
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
                    log.info("Saved/Updated Shipping table (PENDING) for orderId: {} from full_recieved_payments_events", orderId);
                }
            }
        } catch (Exception e) {
            log.error("Error processing Full PaymentEvent: {}", e.getMessage(), e);
        }
    }
}
