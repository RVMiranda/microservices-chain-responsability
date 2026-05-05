package org.rvmiranda.productservice.infraestructure.controller;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.productservice.application.service.ProductService;
import org.rvmiranda.productservice.common.GenericResponse;
import org.rvmiranda.productservice.domain.model.Product;
import org.rvmiranda.productservice.infraestructure.dto.ProductRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping
    public ResponseEntity<GenericResponse<Product>> createProduct(@RequestBody ProductRequest request) {
        try {
            Product newProduct = Product.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .stock(request.getStock())
                    .build();

            Product created = productService.createProduct(newProduct);

            // Emitir evento de éxito
            String requestDataJson = String.format("{\"name\":\"%s\", \"description\":\"%s\", \"price\":%s, \"stock\":%s}",
                    request.getName(), request.getDescription(), request.getPrice(), request.getStock());
            var successEvent = new ProductEvent(
                    UUID.randomUUID().toString(),
                    created.getId(),
                    "CREATE",
                    "SUCCESS",
                    requestDataJson,
                    null,
                    null
            );
            kafkaTemplate.send("inventory_update_events", successEvent);

            return ResponseEntity.ok(GenericResponse.success(created, "Producto creado exitosamente"));
        } catch (Exception e) {
            String requestDataJson = String.format("{\"name\":\"%s\", \"description\":\"%s\", \"price\":%s, \"stock\":%s}",
                    request.getName(), request.getDescription(), request.getPrice(), request.getStock());

            var failedEvent = new ProductEvent(
                    UUID.randomUUID().toString(),
                    "unknown-product-id",
                    "CREATE",
                    "FAILED",
                    requestDataJson,
                    null,
                    e.getMessage()
            );

            kafkaTemplate.send("product-events-retry", failedEvent);

            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Error al crear el producto, evento de reintento enviado a Kafka: " + e.getMessage())
            );
        }
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(GenericResponse.success(products, "Catálogo de productos obtenido"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Product>> getProductById(@PathVariable String id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(GenericResponse.success(product, "Producto encontrado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<Product>> updateProduct(@PathVariable String id, @RequestBody ProductRequest request) {
        try {
            Product updateData = Product.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .stock(request.getStock())
                    .build();

            Product updated = productService.updateProduct(id, updateData);

            // Emitir evento de éxito
            String requestDataJson = String.format("{\"name\":\"%s\", \"description\":\"%s\", \"price\":%s, \"stock\":%s}",
                    request.getName(), request.getDescription(), request.getPrice(), request.getStock());
            var successEvent = new ProductEvent(
                    UUID.randomUUID().toString(),
                    updated.getId(),
                    "UPDATE",
                    "SUCCESS",
                    requestDataJson,
                    null,
                    null
            );
            kafkaTemplate.send("inventory_update_events", successEvent);

            return ResponseEntity.ok(GenericResponse.success(updated, "Producto actualizado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(GenericResponse.success(id, "Producto eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<GenericResponse<String>> reduceStock(@PathVariable String id, @RequestParam Integer quantity) {
        try {
            productService.reduceStock(id, quantity);

            // Emitir evento de éxito
            String requestDataJson = String.format("{\"quantity\":%s}", quantity);
            var successEvent = new ProductEvent(
                    UUID.randomUUID().toString(),
                    id,
                    "REDUCE_STOCK",
                    "SUCCESS",
                    requestDataJson,
                    null,
                    null
            );
            kafkaTemplate.send("inventory_update_events", successEvent);

            return ResponseEntity.ok(GenericResponse.success(id, "Stock reducido exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/test-failure")
    public ResponseEntity<GenericResponse<String>> testFailure() {
        try {
            // Simulated fake internal exception that we catch
            throw new RuntimeException("Simulated error saving product to external system");
        } catch (RuntimeException ex) {
            // Emitting failure event to Kafka
            var failedEvent = new ProductEvent(
                    UUID.randomUUID().toString(),
                    "fake-test-product-id",
                    "CREATE",
                    "FAILED",
                    "{\"name\": \"Test Product\"}",
                    null,
                    ex.getMessage()
            );

            kafkaTemplate.send("product-events-retry", failedEvent);

            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Producto fallido simulado, evento enviado a Kafka.")
            );
        }
    }

    public record ProductEvent(
            String eventId,
            String productId,
            String action,
            String status,
            String requestData,
            String responseData,
            String errorDetails
    ) {}
}
