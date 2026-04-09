package org.rvmiranda.kafkaservice.repository;

import org.rvmiranda.kafkaservice.entities.postgres.ProductRetryJob;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRetryJobRepository extends JpaRepository<ProductRetryJob, UUID> {


    public ProductRetryJob findByProductId(String productId);

    public List<ProductRetryJob> findByStatus(String status);
}