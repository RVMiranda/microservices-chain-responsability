package org.rvmiranda.ordenservice.infraestructure.repository;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.ordenservice.domain.model.Order;
import org.rvmiranda.ordenservice.domain.port.OrderRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    private final SpringDataMongoOrderRepository mongoRepository;

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntity.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .userEmail(order.getUserEmail())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();

        OrderEntity savedEntity = mongoRepository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return mongoRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByUserEmailAndProductId(String userEmail, String productId) {
        return mongoRepository.findByUserEmailAndProductId(userEmail, productId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByUserEmail(String userEmail) {
        return mongoRepository.findByUserEmail(userEmail).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Order toDomain(OrderEntity entity) {
        return Order.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .userEmail(entity.getUserEmail())
                .quantity(entity.getQuantity())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
