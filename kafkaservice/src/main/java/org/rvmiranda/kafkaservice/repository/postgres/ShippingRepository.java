package org.rvmiranda.kafkaservice.repository.postgres;

import org.rvmiranda.kafkaservice.entities.postgres.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, UUID> {
    Optional<Shipping> findByOrderId(String orderId);
    List<Shipping> findByStatus(String status);
}
