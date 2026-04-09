package org.rvmiranda.kafkaservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.application.chain.RetryChainFactory;
import org.rvmiranda.kafkaservice.application.chain.RetryHandler;
import org.rvmiranda.kafkaservice.domain.RetryContext;
import org.rvmiranda.kafkaservice.entities.postgres.OrderRetryJob;
import org.rvmiranda.kafkaservice.entities.postgres.PaymentRetryJob;
import org.rvmiranda.kafkaservice.entities.postgres.ProductRetryJob;
import org.rvmiranda.kafkaservice.repository.postgres.OrderRetryJobRepository;
import org.rvmiranda.kafkaservice.repository.postgres.PaymentRetryJobRepository;
import org.rvmiranda.kafkaservice.repository.postgres.ProductRetryJobRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryProcessingService {

    private final RetryChainFactory chainFactory;
    private final PaymentRetryJobRepository paymentRepository;
    private final OrderRetryJobRepository orderRepository;
    private final ProductRetryJobRepository productRepository;

    public void processRetry(RetryContext context) {
        log.info("Iniciando procesamiento de reintento para ID: {} (Dominio: {})", context.getJobId(), context.getDomainType());

        context.setCurrentAttempt(context.getCurrentAttempt() + 1);

        try {
            RetryHandler chain = chainFactory.buildChain();
            chain.process(context);
        } catch (Exception e) {
            log.error("Error procesando reintento para ID: {}", context.getJobId(), e);
            handleFailure(context, e);
        }
    }

    private void handleFailure(RetryContext context, Exception exception) {
        log.info("Simulando envío de correo de FALLA para {} con ID {}", context.getDomainType(), context.getJobId());

        try {
            switch (context.getDomainType()) {
                case "PAYMENT":
                    PaymentRetryJob payment = paymentRepository.findById(context.getJobId()).orElse(null);
                    if (payment != null) {
                        payment.setStatus("FAILED");
                        payment.setAttempt(context.getCurrentAttempt());
                        paymentRepository.save(payment);
                    }
                    break;
                case "ORDER":
                    OrderRetryJob order = orderRepository.findById(context.getJobId()).orElse(null);
                    if (order != null) {
                        order.setStatus("FAILED");
                        order.setAttempt(context.getCurrentAttempt());
                        orderRepository.save(order);
                    }
                    break;
                case "PRODUCT":
                    ProductRetryJob product = productRepository.findById(context.getJobId()).orElse(null);
                    if (product != null) {
                        product.setStatus("FAILED");
                        product.setAttempt(context.getCurrentAttempt());
                        productRepository.save(product);
                    }
                    break;
                default:
                    log.error("Dominio desconocido al intentar marcar como FAILED: {}", context.getDomainType());
            }
            log.info("Estado actualizado a FAILED satisfactoriamente.");
        } catch (Exception dbError) {
            log.error("Error crítico actualizando registro a FAILED", dbError);
        }
    }
}
