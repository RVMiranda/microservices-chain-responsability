package org.rvmiranda.pagoservice.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpringDataMongoPaymentRepository extends MongoRepository<PaymentEntity, String> {
    Optional<PaymentEntity> findByOrderId(String orderId);
}
