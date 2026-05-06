package org.rvmiranda.pagoservice.domain.port;

import org.rvmiranda.pagoservice.domain.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    Optional<Payment> findById(String id);
    List<Payment> findAll();
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findAllByOrderId(String orderId);
}
