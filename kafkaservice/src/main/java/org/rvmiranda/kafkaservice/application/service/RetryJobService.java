package org.rvmiranda.kafkaservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.model.ProductEvent;
import org.rvmiranda.kafkaservice.domain.model.RetryJob;
import org.rvmiranda.kafkaservice.domain.port.RetryJobRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryJobService {

    private final RetryJobRepositoryPort repositoryPort;

    public void processFailedEvent(ProductEvent event) {
        log.info("Processing failed product event: {}", event);

        RetryJob job = RetryJob.builder()
                .id(UUID.randomUUID())
                .productId(event.getProductId())
                .requestData(event.getRequestData())
                .responseData(event.getResponseData())
                .action(event.getAction())
                .attempt(0)
                .status("SCHEDULED")
                .nextRunAt(OffsetDateTime.now().plusMinutes(5)) // standard exponential backoff delay base
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        RetryJob saved = repositoryPort.save(job);
        log.info("Successfully scheduled a new retry job with ID {} for product {}", saved.getId(), saved.getProductId());
    }
}
