package org.rvmiranda.ordenservice.infraestructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface SpringDataMongoOrderBalanceRepository extends MongoRepository<OrderBalanceEntity, String> {
    Optional<OrderBalanceEntity> findByOrderId(String orderId);
}
