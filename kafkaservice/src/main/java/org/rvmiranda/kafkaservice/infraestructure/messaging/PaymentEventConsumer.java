package org.rvmiranda.kafkaservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.model.PaymentEvent;
import org.rvmiranda.kafkaservice.entities.postgres.PaymentRetryJob;
import org.rvmiranda.kafkaservice.repository.postgres.PaymentRetryJobRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentRetryJobRepository paymentRetryJobRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-events", groupId = "payment-events-retry-listener", containerFactory = "paymentEventKafkaListenerContainerFactory")
    public void consume(PaymentEvent event) {
        log.info("Received PaymentEvent: {}", event);
        if ("FAILED".equalsIgnoreCase(event.getStatus())) {
            log.warn("Detected FAILED event for payment {}. Saving retry job.", event.getPaymentId());
            try {
                JsonNode requestDataNode = event.getRequestData() != null
                        ? objectMapper.readTree(event.getRequestData())
                        : objectMapper.createObjectNode();

                PaymentRetryJob job = PaymentRetryJob.builder()
                        .paymentId(event.getPaymentId())
                        .requestData(requestDataNode)
                        .action(event.getAction())
                        .attempt(0)
                        .status("SCHEDULED")
                        .nextRunAt(OffsetDateTime.now().plusMinutes(5))
                        .build();

                paymentRetryJobRepository.save(job);
                log.info("Saved PaymentRetryJob with status SCHEDULED for paymentId: {}", event.getPaymentId());
            } catch (Exception e) {
                log.error("Error saving PaymentRetryJob for paymentId {}: {}", event.getPaymentId(), e.getMessage(), e);
            }
        } else {
            log.info("PaymentEvent {} processed successfully without failure.", event.getEventId());
        }
    }
}
