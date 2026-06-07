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

import org.rvmiranda.ordenservice.infraestructure.client.ProductClient;
import org.rvmiranda.ordenservice.infraestructure.dto.ProductDto;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductClient productClient;

    @PostMapping
    public ResponseEntity<GenericResponse<Order>> createOrder(@RequestBody OrderRequest request) {
        try {
            // Obtener detalles del producto sincrónicamente
            GenericResponse<ProductDto> productResponse = productClient.getProductById(request.getProductId());
            if (productResponse == null || productResponse.getData() == null) {
                return ResponseEntity.badRequest().body(GenericResponse.error("Error: Producto no encontrado"));
            }
            ProductDto product = productResponse.getData();
            
            String productName = product.getName();
            Double totalPrice = product.getPrice() * request.getQuantity();

            Order createdOrder = orderService.createOrder(
                    request.getProductId(),
                    productName,
                    totalPrice,
                    request.getQuantity(),
                    request.getUserEmail()
            );

            // Emitir evento para actualizar inventario (inventory_update_events)
            String inventoryDataJson = String.format("{\"productId\":\"%s\", \"quantity\":%s}",
                    request.getProductId(), request.getQuantity());
            var inventoryEvent = new OrderEvent(
                    UUID.randomUUID().toString(),
                    createdOrder.getId(),
                    "CREATE_ORDER_INVENTORY",
                    "SUCCESS",
                    inventoryDataJson,
                    null,
                    null
            );
            kafkaTemplate.send("inventory_update_events", inventoryEvent);

            // Emitir evento de estado de orden (order_status_changed_events)
            String statusDataJson = String.format("{\"status\":\"%s\"}", createdOrder.getStatus());
            var statusEvent = new OrderEvent(
                    UUID.randomUUID().toString(),
                    createdOrder.getId(),
                    "CREATE_ORDER_STATUS",
                    "SUCCESS",
                    statusDataJson,
                    null,
                    null
            );
            kafkaTemplate.send("order-events", statusEvent);
            kafkaTemplate.send("order-status-changed-events", statusEvent);

            return ResponseEntity.ok(GenericResponse.success(createdOrder, "Orden creada exitosamente y eventos asíncronos enviados"));
        } catch (Exception e) {
            String requestDataJson = String.format("{\"productId\":\"%s\", \"quantity\":%s, \"userEmail\":\"%s\"}",
                    request.getProductId(), request.getQuantity(), request.getUserEmail());

            var failedEvent = new OrderEvent(
                    UUID.randomUUID().toString(),
                    "unknown-order-id",
                    "CREATE",
                    "FAILED",
                    requestDataJson,
                    null,
                    e.getMessage()
            );
            kafkaTemplate.send("order-events-retry", failedEvent);

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
            // Obtener la orden antes de actualizar para tener los datos de inventario
            Order existingOrder = orderService.getOrderById(id);
            
            orderService.updateOrderStatus(id, status);

            // Si se cancela la orden, emitir evento para restaurar el inventario
            if ("CANCELADA".equalsIgnoreCase(status)) {
                String inventoryDataJson = String.format("{\"productId\":\"%s\", \"quantity\":%s}",
                        existingOrder.getProductId(), existingOrder.getQuantity());
                var inventoryEvent = new OrderEvent(
                        UUID.randomUUID().toString(),
                        id,
                        "RESTORE_ORDER_INVENTORY",
                        "SUCCESS",
                        inventoryDataJson,
                        null,
                        null
                );
                kafkaTemplate.send("inventory_update_events", inventoryEvent);
            }

            // Emitir evento de estado de orden (order-events)
            String statusDataJson = String.format("{\"status\":\"%s\"}", status);
            var statusEvent = new OrderEvent(
                    UUID.randomUUID().toString(),
                    id,
                    "UPDATE_ORDER_STATUS",
                    "SUCCESS",
                    statusDataJson,
                    null,
                    null
            );
            kafkaTemplate.send("order-events", statusEvent);
            kafkaTemplate.send("order-status-changed-events", statusEvent);

            return ResponseEntity.ok(GenericResponse.success(id, "Estado actualizado a " + status + " y evento asíncrono enviado"));
        } catch (Exception e) {
            String requestDataJson = String.format("{\"status\":\"%s\"}", status);

            var failedEvent = new OrderEvent(
                    UUID.randomUUID().toString(),
                    id,
                    "UPDATE",
                    "FAILED",
                    requestDataJson,
                    null,
                    e.getMessage()
            );
            kafkaTemplate.send("order-events-retry", failedEvent);

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

    @GetMapping("/{id}/saldo-restante")
    public ResponseEntity<GenericResponse<Double>> getOrderRemainingBalance(@PathVariable String id) {
        try {
            Double remaining = orderService.getOrderRemainingBalance(id);
            return ResponseEntity.ok(GenericResponse.success(remaining, "Saldo restante obtenido"));
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
            var failedEvent = new OrderEvent(
                    UUID.randomUUID().toString(),
                    "fake-test-order-id",
                    "CREATE",
                    "FAILED",
                    "{\"productId\": \"prod-001\", \"quantity\": 2}",
                    null,
                    ex.getMessage()
            );
            kafkaTemplate.send("order-events-retry", failedEvent);
            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Orden fallida simulada, evento enviado a Kafka.")
            );
        }
    }

    public record OrderEvent(
            String eventId,
            String orderId,
            String action,
            String status,
            String requestData,
            String responseData,
            String errorDetails
    ) {}
}
