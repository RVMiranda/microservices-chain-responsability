package org.rvmiranda.ordenservice.infraestructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpringDataMongoOrderRepository extends MongoRepository<OrderEntity, String> {
    List<OrderEntity> findByUserEmailAndProductId(String userEmail, String productId);
    List<OrderEntity> findByUserEmail(String userEmail);
    List<OrderEntity> findByProductId(String productId);
}
