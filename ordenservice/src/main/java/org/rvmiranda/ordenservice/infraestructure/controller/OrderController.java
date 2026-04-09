package org.rvmiranda.ordenservice.infraestructure.controller;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.ordenservice.application.service.OrderService;
import org.rvmiranda.ordenservice.common.GenericResponse;
import org.rvmiranda.ordenservice.domain.model.Order;
import org.rvmiranda.ordenservice.infraestructure.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<GenericResponse<Order>> createOrder(@RequestBody OrderRequest request) {
        try {
            Order createdOrder = orderService.createOrder(request.getProductId(), request.getQuantity(),  request.getUserEmail());
            return ResponseEntity.ok(GenericResponse.success(createdOrder, "Orden creada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
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
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
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

}
