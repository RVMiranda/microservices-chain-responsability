package org.rvmiranda.kafkaservice.application.chain.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.application.chain.AbstractRetryHandler;
import org.rvmiranda.kafkaservice.domain.RetryContext;
import org.rvmiranda.kafkaservice.entities.postgres.OrderRetryJob;
import org.rvmiranda.kafkaservice.entities.postgres.PaymentRetryJob;
import org.rvmiranda.kafkaservice.entities.postgres.ProductRetryJob;
import org.rvmiranda.kafkaservice.repository.postgres.OrderRetryJobRepository;
import org.rvmiranda.kafkaservice.repository.postgres.PaymentRetryJobRepository;
import org.rvmiranda.kafkaservice.repository.postgres.ProductRetryJobRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDatabaseHandler extends AbstractRetryHandler {

    private final PaymentRetryJobRepository paymentRepository;
    private final OrderRetryJobRepository orderRepository;
    private final ProductRetryJobRepository productRepository;

    @Override
    protected void execute(RetryContext context) {
        log.info("Paso C - UpdateDatabaseHandler: Actualizando estado a SUCCESS para el contexto: {}", context.getJobId());

        try {
            switch (context.getDomainType()) {
                case "PAYMENT":
                    PaymentRetryJob payment = paymentRepository.findById(context.getJobId())
                            .orElseThrow(() -> new RuntimeException("PaymentRetryJob no encontrado."));
                    payment.setStatus("SUCCESS");
                    payment.setAttempt(context.getCurrentAttempt());
                    paymentRepository.save(payment);
                    break;
                case "ORDER":
                    OrderRetryJob order = orderRepository.findById(context.getJobId())
                            .orElseThrow(() -> new RuntimeException("OrderRetryJob no encontrado."));
                    order.setStatus("SUCCESS");
                    order.setAttempt(context.getCurrentAttempt());
                    orderRepository.save(order);
                    break;
                case "PRODUCT":
                    ProductRetryJob product = productRepository.findById(context.getJobId())
                            .orElseThrow(() -> new RuntimeException("ProductRetryJob no encontrado."));
                    product.setStatus("SUCCESS");
                    product.setAttempt(context.getCurrentAttempt());
                    productRepository.save(product);
                    break;
                default:
                    throw new IllegalArgumentException("DomainType no soportado: " + context.getDomainType());
            }
            log.info("Paso C exitoso: Registro de la base de datos actualizado a SUCCESS.");
            
        } catch (Exception e) {
            log.error("Paso C falló: Problema al actualizar la base de datos.", e);
            throw new RuntimeException("Fallo en paso C de la Cadena: Update en DB fracasó.", e);
        }
    }
}
