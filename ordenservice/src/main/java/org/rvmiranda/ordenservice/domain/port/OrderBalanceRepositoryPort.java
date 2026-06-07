package org.rvmiranda.ordenservice.domain.port;

import org.rvmiranda.ordenservice.domain.model.OrderBalance;
import java.util.Optional;

public interface OrderBalanceRepositoryPort {
    OrderBalance save(OrderBalance orderBalance);
    Optional<OrderBalance> findByOrderId(String orderId);
}
