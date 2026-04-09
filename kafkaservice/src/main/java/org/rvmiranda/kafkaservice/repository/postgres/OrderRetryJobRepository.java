package org.rvmiranda.kafkaservice.repository.postgres;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.rvmiranda.kafkaservice.entities.postgres.OrderRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRetryJobRepository extends JpaRepository<OrderRetryJob, UUID> {
    List<OrderRetryJob> findByStatusAndNextRunAtBefore(String status, OffsetDateTime now);
}
