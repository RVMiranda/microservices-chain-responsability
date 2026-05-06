package org.rvmiranda.pagoservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.pagoservice.domain.model.Payment;
import org.rvmiranda.pagoservice.domain.port.PaymentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {
    private final SpringDataMongoPaymentRepository mongoRepository;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userEmail(payment.getUserEmail())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .processedAt(payment.getProcessedAt())
                .build();

        PaymentEntity savedEntity = mongoRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return mongoRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAllByOrderId(String orderId) {
        return mongoRepository.findAllByOrderId(orderId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Payment toDomain(PaymentEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .userEmail(entity.getUserEmail())
                .amount(entity.getAmount())
                .paymentMethod(entity.getPaymentMethod())
                .status(entity.getStatus())
                .processedAt(entity.getProcessedAt())
                .build();
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return mongoRepository.findByOrderId(orderId).map(this::toDomain);
    }
}
