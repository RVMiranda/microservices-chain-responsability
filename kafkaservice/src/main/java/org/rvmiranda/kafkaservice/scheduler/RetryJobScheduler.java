package org.rvmiranda.kafkaservice.scheduler;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.RetryContext;
import org.rvmiranda.kafkaservice.entities.postgres.OrderRetryJob;
import org.rvmiranda.kafkaservice.entities.postgres.PaymentRetryJob;
import org.rvmiranda.kafkaservice.entities.postgres.ProductRetryJob;
import org.rvmiranda.kafkaservice.repository.postgres.OrderRetryJobRepository;
import org.rvmiranda.kafkaservice.repository.postgres.PaymentRetryJobRepository;
import org.rvmiranda.kafkaservice.repository.postgres.ProductRetryJobRepository;
import org.rvmiranda.kafkaservice.services.RetryProcessingService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class RetryJobScheduler {

    private final PaymentRetryJobRepository paymentRepository;
    private final OrderRetryJobRepository orderRepository;
    private final ProductRetryJobRepository productRepository;
    private final RetryProcessingService retryProcessingService;

    @Scheduled(fixedDelay = 10000)
    public void processPaymentRetries() {
        log.info("Running scheduled task for SCHEDULED PaymentRetryJobs...");
        List<PaymentRetryJob> jobs = paymentRepository.findByStatusAndNextRunAtBefore("SCHEDULED", OffsetDateTime.now());
        for (PaymentRetryJob job : jobs) {
            RetryContext context = RetryContext.builder()
                    .jobId(job.getId())
                    .domainType("PAYMENT")
                    .entityId(job.getPaymentId())
                    .requestData(job.getRequestData())
                    .responseData(job.getResponseData())
                    .currentAttempt(job.getAttempt())
                    .build();
            retryProcessingService.processRetry(context);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void processOrderRetries() {
        log.info("Running scheduled task for SCHEDULED OrderRetryJobs...");
        List<OrderRetryJob> jobs = orderRepository.findByStatusAndNextRunAtBefore("SCHEDULED", OffsetDateTime.now());
        for (OrderRetryJob job : jobs) {
            RetryContext context = RetryContext.builder()
                    .jobId(job.getId())
                    .domainType("ORDER")
                    .entityId(job.getOrderId())
                    .requestData(job.getRequestData())
                    .responseData(job.getResponseData())
                    .currentAttempt(job.getAttempt())
                    .build();
            retryProcessingService.processRetry(context);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void processProductRetries() {
        log.info("Running scheduled task for SCHEDULED ProductRetryJobs...");
        List<ProductRetryJob> jobs = productRepository.findByStatusAndNextRunAtBefore("SCHEDULED", OffsetDateTime.now());
        for (ProductRetryJob job : jobs) {
            RetryContext context = RetryContext.builder()
                    .jobId(job.getId())
                    .domainType("PRODUCT")
                    .entityId(job.getProductId())
                    .requestData(job.getRequestData())
                    .responseData(job.getResponseData())
                    .currentAttempt(job.getAttempt())
                    .build();
            retryProcessingService.processRetry(context);
        }
    }
}
