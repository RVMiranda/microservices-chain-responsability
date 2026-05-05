package org.rvmiranda.kafkaservice.infraestructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.model.OrderEvent;
import org.rvmiranda.kafkaservice.entities.postgres.OrderRetryJob;
import org.rvmiranda.kafkaservice.repository.postgres.OrderRetryJobRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderRetryJobRepository orderRetryJobRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events-retry", groupId = "order-events-retry-listener", containerFactory = "orderEventKafkaListenerContainerFactory")
    public void consume(OrderEvent event) {
        log.info("Received OrderEvent: {}", event);
        if ("FAILED".equalsIgnoreCase(event.getStatus())) {
            log.warn("Detected FAILED event for order {}. Saving retry job.", event.getOrderId());
            try {
                JsonNode requestDataNode = event.getRequestData() != null
                        ? objectMapper.readTree(event.getRequestData())
                        : objectMapper.createObjectNode();

                OrderRetryJob job = OrderRetryJob.builder()
                        .orderId(event.getOrderId())
                        .requestData(requestDataNode)
                        .action(event.getAction())
                        .attempt(0)
                        .status("SCHEDULED")
                        .nextRunAt(OffsetDateTime.now().plusMinutes(5))
                        .build();

                orderRetryJobRepository.save(job);
                log.info("Saved OrderRetryJob with status SCHEDULED for orderId: {}", event.getOrderId());
            } catch (Exception e) {
                log.error("Error saving OrderRetryJob for orderId {}: {}", event.getOrderId(), e.getMessage(), e);
            }
        } else {
            log.info("OrderEvent {} processed successfully without failure.", event.getEventId());
        }
    }
}
