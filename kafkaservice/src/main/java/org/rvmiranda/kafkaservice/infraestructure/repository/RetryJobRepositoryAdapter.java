package org.rvmiranda.kafkaservice.infraestructure.repository;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.kafkaservice.domain.model.RetryJob;
import org.rvmiranda.kafkaservice.domain.port.RetryJobRepositoryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetryJobRepositoryAdapter implements RetryJobRepositoryPort {

    private final JpaRetryJobRepository jpaRepository;

    @Override
    public RetryJob save(RetryJob retryJob) {
        RetryJobEntity entity = RetryJobEntity.builder()
                .id(retryJob.getId())
                .productId(retryJob.getProductId())
                .requestData(retryJob.getRequestData())
                .responseData(retryJob.getResponseData())
                .action(retryJob.getAction())
                .attempt(retryJob.getAttempt())
                .status(retryJob.getStatus())
                .nextRunAt(retryJob.getNextRunAt())
                .createdAt(retryJob.getCreatedAt())
                .updatedAt(retryJob.getUpdatedAt())
                .build();
        
        RetryJobEntity saved = jpaRepository.save(entity);

        return RetryJob.builder()
                .id(saved.getId())
                .productId(saved.getProductId())
                .requestData(saved.getRequestData())
                .responseData(saved.getResponseData())
                .action(saved.getAction())
                .attempt(saved.getAttempt())
                .status(saved.getStatus())
                .nextRunAt(saved.getNextRunAt())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
