package org.rvmiranda.kafkaservice.infraestructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaRetryJobRepository extends JpaRepository<RetryJobEntity, UUID> {
}
