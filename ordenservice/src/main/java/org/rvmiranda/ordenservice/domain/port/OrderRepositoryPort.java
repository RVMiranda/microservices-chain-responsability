package org.rvmiranda.ordenservice.domain.port;

import org.rvmiranda.ordenservice.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    List<Order> findByUserEmailAndProductId(String userEmail, String productId);
    List<Order> findByUserEmail(String userEmail);
    List<Order> findByProductId(String productId);
}
