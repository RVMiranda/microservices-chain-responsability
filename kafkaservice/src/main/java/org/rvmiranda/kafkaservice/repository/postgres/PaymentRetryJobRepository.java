package org.rvmiranda.kafkaservice.repository.postgres;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.rvmiranda.kafkaservice.entities.postgres.PaymentRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRetryJobRepository extends JpaRepository<PaymentRetryJob, UUID> {
    List<PaymentRetryJob> findByStatusAndNextRunAtBefore(String status, OffsetDateTime now);
}
