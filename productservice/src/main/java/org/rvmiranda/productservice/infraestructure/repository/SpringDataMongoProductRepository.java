package org.rvmiranda.productservice.infraestructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataMongoProductRepository extends MongoRepository<ProductEntity, String> {
}