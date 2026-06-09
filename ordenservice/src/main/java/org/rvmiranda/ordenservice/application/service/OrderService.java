package org.rvmiranda.ordenservice.application.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.rvmiranda.ordenservice.common.GenericResponse;
import org.rvmiranda.ordenservice.domain.model.Order;
import org.rvmiranda.ordenservice.domain.port.OrderRepositoryPort;

import org.rvmiranda.ordenservice.domain.model.OrderBalance;
import org.rvmiranda.ordenservice.domain.port.OrderBalanceRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderBalanceRepositoryPort orderBalanceRepositoryPort;

    public Order createOrder(String productId, String productName, Double totalPrice, Integer quantity, String userEmail) {
        // 1. Crear y guardar la orden directamente (asíncrono)
        Order newOrder = Order.builder()
                .productId(productId)
                .productName(productName)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .status("CREADA") // El pago se procesará después en el flujo
                .userEmail(userEmail)
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepositoryPort.save(newOrder);

        // Inicializar el saldo restante en la nueva colección
        OrderBalance orderBalance = OrderBalance.builder()
                .orderId(savedOrder.getId())
                .remainingBalance(totalPrice)
                .updatedAt(LocalDateTime.now())
                .build();
        orderBalanceRepositoryPort.save(orderBalance);

        return savedOrder;
    }

    public Order getOrderById(String id) {
        return orderRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Orden no encontrada con id " + id));
    }

    public void updateOrderStatus(String id, String newStatus) {
        Order order = getOrderById(id);
        order.setStatus(newStatus);
        orderRepositoryPort.save(order);
    }

    public List<Order> getOrdersByUserEmail(String userEmail) {
        return orderRepositoryPort.findByUserEmail(userEmail);
    }

    public List<Order> getOrdersByEmailAndProduct(String email, String productId) {
        return orderRepositoryPort.findByUserEmailAndProductId(email, productId);
    }

    public List<Order> getAllOrders() {
        return orderRepositoryPort.findAll();
    }

    public void updateOrderRemainingBalance(String orderId, Double remainingBalance) {
        OrderBalance orderBalance = orderBalanceRepositoryPort.findByOrderId(orderId)
                .orElse(OrderBalance.builder()
                        .orderId(orderId)
                        .build());
        orderBalance.setRemainingBalance(remainingBalance);
        orderBalance.setUpdatedAt(LocalDateTime.now());
        orderBalanceRepositoryPort.save(orderBalance);
    }

    public Double getOrderRemainingBalance(String orderId) {
        return orderBalanceRepositoryPort.findByOrderId(orderId)
                .map(OrderBalance::getRemainingBalance)
                .orElseGet(() -> {
                    try {
                        return getOrderById(orderId).getTotalPrice();
                    } catch (Exception e) {
                        return 0.0;
                    }
                });
    }

    public boolean existsOrderByProductId(String productId) {
        return !orderRepositoryPort.findByProductId(productId).isEmpty();
    }
}
