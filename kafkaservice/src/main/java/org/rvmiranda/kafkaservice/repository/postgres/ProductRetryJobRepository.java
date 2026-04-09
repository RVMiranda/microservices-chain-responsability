package org.rvmiranda.kafkaservice.repository.postgres;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.rvmiranda.kafkaservice.entities.postgres.ProductRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRetryJobRepository extends JpaRepository<ProductRetryJob, UUID> {
    List<ProductRetryJob> findByStatusAndNextRunAtBefore(String status, OffsetDateTime now);
}
