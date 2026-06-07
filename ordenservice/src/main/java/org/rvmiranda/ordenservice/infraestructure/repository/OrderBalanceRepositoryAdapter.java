package org.rvmiranda.ordenservice.infraestructure.repository;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.ordenservice.domain.model.OrderBalance;
import org.rvmiranda.ordenservice.domain.port.OrderBalanceRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderBalanceRepositoryAdapter implements OrderBalanceRepositoryPort {
    private final SpringDataMongoOrderBalanceRepository mongoRepository;

    @Override
    public OrderBalance save(OrderBalance orderBalance) {
        OrderBalanceEntity entity = OrderBalanceEntity.builder()
                .id(orderBalance.getId())
                .orderId(orderBalance.getOrderId())
                .remainingBalance(orderBalance.getRemainingBalance())
                .updatedAt(orderBalance.getUpdatedAt())
                .build();

        OrderBalanceEntity savedEntity = mongoRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<OrderBalance> findByOrderId(String orderId) {
        return mongoRepository.findByOrderId(orderId).map(this::toDomain);
    }

    private OrderBalance toDomain(OrderBalanceEntity entity) {
        return OrderBalance.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .remainingBalance(entity.getRemainingBalance())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
