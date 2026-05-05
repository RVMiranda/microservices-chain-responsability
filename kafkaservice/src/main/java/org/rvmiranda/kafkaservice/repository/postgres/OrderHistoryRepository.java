package org.rvmiranda.kafkaservice.repository.postgres;

import org.rvmiranda.kafkaservice.entities.postgres.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, UUID> {
}
