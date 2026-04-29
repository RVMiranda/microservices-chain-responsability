package org.rvmiranda.ordenservice.infraestructure.controller;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.ordenservice.application.service.OrderService;
import org.rvmiranda.ordenservice.common.GenericResponse;
import org.rvmiranda.ordenservice.domain.model.Order;
import org.rvmiranda.ordenservice.infraestructure.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping
    public ResponseEntity<GenericResponse<Order>> createOrder(@RequestBody OrderRequest request) {
        try {
            Order createdOrder = orderService.createOrder(request.getProductId(), request.getQuantity(), request.getUserEmail());
            return ResponseEntity.ok(GenericResponse.success(createdOrder, "Orden creada exitosamente"));
        } catch (Exception e) {
            String requestDataJson = String.format("{\"productId\":\"%s\", \"quantity\":%s, \"userEmail\":\"%s\"}",
                    request.getProductId(), request.getQuantity(), request.getUserEmail());

            var failedEvent = new FailedOrderEvent(
                    UUID.randomUUID().toString(),
                    "unknown-order-id",
                    "CREATE",
                    "FAILED",
                    requestDataJson,
                    null,
                    e.getMessage()
            );
            kafkaTemplate.send("order-events", failedEvent);

            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Error al crear la orden, evento de reintento enviado a Kafka: " + e.getMessage())
            );
        }
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(GenericResponse.success(orders, "Lista de órdenes obtenida"));
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<GenericResponse<String>> updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        try {
            orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(GenericResponse.success(id, "Estado actualizado a " + status));
        } catch (Exception e) {
            String requestDataJson = String.format("{\"status\":\"%s\"}", status);

            var failedEvent = new FailedOrderEvent(
                    UUID.randomUUID().toString(),
                    id,
                    "UPDATE",
                    "FAILED",
                    requestDataJson,
                    null,
                    e.getMessage()
            );
            kafkaTemplate.send("order-events", failedEvent);

            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Error al actualizar la orden, evento de reintento enviado a Kafka: " + e.getMessage())
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Order>> getOrderById(@PathVariable String id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(GenericResponse.success(order, "Orden encontrada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<GenericResponse<List<Order>>> getOrdersByUserId(@PathVariable String email) {
        List<Order> orders = orderService.getOrdersByUserEmail(email);
        return ResponseEntity.ok(GenericResponse.success(orders, "Órdenes del usuario obtenidas"));
    }

    @GetMapping("/usuario/email/{email}/producto/{productId}")
    public ResponseEntity<GenericResponse<List<Order>>> getOrdersByEmailAndProduct(
            @PathVariable String email,
            @PathVariable String productId) {
        try {
            List<Order> orders = orderService.getOrdersByEmailAndProduct(email, productId);
            return ResponseEntity.ok(GenericResponse.success(orders, "Búsqueda de órdenes completada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }

    // ── Test: simula un fallo de orden y publica el evento a Kafka ────────────
    @PostMapping("/test-failure")
    public ResponseEntity<GenericResponse<String>> testFailure() {
        try {
            throw new RuntimeException("Simulated error processing order in external system");
        } catch (RuntimeException ex) {
            var failedEvent = new FailedOrderEvent(
                    UUID.randomUUID().toString(),
                    "fake-test-order-id",
                    "CREATE",
                    "FAILED",
                    "{\"productId\": \"prod-001\", \"quantity\": 2}",
                    null,
                    ex.getMessage()
            );
            kafkaTemplate.send("order-events", failedEvent);
            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Orden fallida simulada, evento enviado a Kafka.")
            );
        }
    }

    public record FailedOrderEvent(
            String eventId,
            String orderId,
            String action,
            String status,
            String requestData,
            String responseData,
            String errorDetails
    ) {}
}
