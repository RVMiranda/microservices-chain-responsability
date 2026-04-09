package org.rvmiranda.ordenservice.application.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.rvmiranda.ordenservice.common.GenericResponse;
import org.rvmiranda.ordenservice.domain.model.Order;
import org.rvmiranda.ordenservice.domain.port.OrderRepositoryPort;
import org.rvmiranda.ordenservice.infraestructure.client.ProductClient;
import org.rvmiranda.ordenservice.infraestructure.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductClient productClient;

    public Order createOrder(String productId, Integer quantity, String userEmail) {
        ProductDto product;

        // 1. Preguntarle al ProductService si el producto existe
        try {
            GenericResponse<ProductDto> response = productClient.getProductById(productId);
            product = response.getData();
        } catch (FeignException e) {
            // Si productservice devuelve un 400 o 404, Feign lanza esta excepción
            throw new RuntimeException("Error: El producto con ID " + productId + " no existe o el catálogo no responde.");
        }

        // 2. Regla de negocio: Verificar si hay suficiente stock
        if (product.getStock() < quantity) {
            throw new RuntimeException("Error: Stock insuficiente. Solo quedan " + product.getStock() + " unidades de " + product.getName());
        }

        // 3. Regla de negocio: Calcular el precio total
        Double total = product.getPrice() * quantity;

        // 4. Avisar a productservice que reste del stock
        productClient.reduceStock(productId, quantity);

        // 5. Crear y guardar la orden
        Order newOrder = Order.builder()
                .productId(productId)
                .productName(product.getName())
                .quantity(quantity)
                .totalPrice(total)
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
