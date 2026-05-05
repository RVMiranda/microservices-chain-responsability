package org.rvmiranda.ordenservice.application.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.rvmiranda.ordenservice.common.GenericResponse;
import org.rvmiranda.ordenservice.domain.model.Order;
import org.rvmiranda.ordenservice.domain.port.OrderRepositoryPort;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepositoryPort orderRepositoryPort;

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

        return orderRepositoryPort.save(newOrder);
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
}
