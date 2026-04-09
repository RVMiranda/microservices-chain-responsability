package org.rvmiranda.kafkaservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.dto.RetryJobPayload;
import org.rvmiranda.kafkaservice.entities.postgres.OrderRetryJob;
import org.rvmiranda.kafkaservice.entities.postgres.PaymentRetryJob;
import org.rvmiranda.kafkaservice.entities.postgres.ProductRetryJob;
import org.rvmiranda.kafkaservice.repository.postgres.OrderRetryJobRepository;
import org.rvmiranda.kafkaservice.repository.postgres.PaymentRetryJobRepository;
import org.rvmiranda.kafkaservice.repository.postgres.ProductRetryJobRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryKafkaListener {

    private final PaymentRetryJobRepository paymentRepository;
    private final OrderRetryJobRepository orderRepository;
    private final ProductRetryJobRepository productRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payments_retry_jobs", groupId = "${spring.kafka.consumer.group-id:retry-group}")
    public void listenPaymentsRetryJobs(String payload) {
        log.info("Received Payment Retry Job Payload: {}", payload);
        try {
            RetryJobPayload retryPayload = objectMapper.readValue(payload, RetryJobPayload.class);
            String paymentId = extractId(retryPayload);

            PaymentRetryJob job = PaymentRetryJob.builder()
                    .paymentId(paymentId)
                    .requestData(retryPayload.getData())
                    .action("RETRY_PAYMENT")
                    .attempt(0)
                    .status("SCHEDULED")
                    .build();

            paymentRepository.save(job);
            log.info("Saved PaymentRetryJob with status SCHEDULED for paymentId: {}", paymentId);
        } catch (Exception e) {
            log.error("Error processing message from payments_retry_jobs: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "order_retry_jobs", groupId = "${spring.kafka.consumer.group-id:retry-group}")
    public void listenOrderRetryJobs(String payload) {
        log.info("Received Order Retry Job Payload: {}", payload);
        try {
            RetryJobPayload retryPayload = objectMapper.readValue(payload, RetryJobPayload.class);
            String orderId = extractId(retryPayload);

            OrderRetryJob job = OrderRetryJob.builder()
                    .orderId(orderId)
                    .requestData(retryPayload.getData())
                    .action("RETRY_ORDER")
                    .attempt(0)
                    .status("SCHEDULED")
                    .build();

            orderRepository.save(job);
            log.info("Saved OrderRetryJob with status SCHEDULED for orderId: {}", orderId);
        } catch (Exception e) {
            log.error("Error processing message from order_retry_jobs: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "product_retry_jobs", groupId = "${spring.kafka.consumer.group-id:retry-group}")
    public void listenProductRetryJobs(String payload) {
        log.info("Received Product Retry Job Payload: {}", payload);
        try {
            RetryJobPayload retryPayload = objectMapper.readValue(payload, RetryJobPayload.class);
            String productId = extractId(retryPayload);

            ProductRetryJob job = ProductRetryJob.builder()
                    .productId(productId)
                    .requestData(retryPayload.getData())
                    .action("RETRY_PRODUCT")
                    .attempt(0)
                    .status("SCHEDULED")
                    .build();

            productRepository.save(job);
            log.info("Saved ProductRetryJob with status SCHEDULED for productId: {}", productId);
        } catch (Exception e) {
            log.error("Error processing message from product_retry_jobs: {}", e.getMessage(), e);
        }
    }

    private String extractId(RetryJobPayload payload) {
        if (payload != null && payload.getData() != null && payload.getData().has("id")) {
            return payload.getData().get("id").asText();
        }
        return "UNKNOWN";
    }
}
