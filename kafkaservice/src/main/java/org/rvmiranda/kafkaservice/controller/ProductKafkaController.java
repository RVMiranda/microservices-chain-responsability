package org.rvmiranda.kafkaservice.controller;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.kafkaservice.dto.ProductDto;
import org.rvmiranda.kafkaservice.kafka.ProductProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka/productos")
@RequiredArgsConstructor
public class ProductKafkaController {
    private final ProductProducer productProducer;

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarProducto(@RequestBody ProductDto product) {
        productProducer.enviarProducto(product);
        return ResponseEntity.ok("Producto enviado al topic: " + product.getName());
    }

    @GetMapping("/test")
    public ResponseEntity<String> disparoDePrueba() {
        // Creamos un producto de prueba "quemado" en el código
        ProductDto testProduct = new ProductDto();
        testProduct.setName("Laptop Alienware");
        testProduct.setDescription("Laptop gamer marca Alienware con lo mejor del mercado");
        testProduct.setPrice(33000.00);


        // Lo disparamos a Kafka
        productProducer.enviarProducto(testProduct);

        return ResponseEntity.ok("¡Disparo exitoso! Revisa Kafka UI para ver la Laptop Alienware.");
    }

    // Se comentó este endpoint porque "producService" no existe en este microservicio.
    // @PostMapping("/create-product")
    // public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto product) {
    //     try{
    //         return ResponseEntity.ok(producService.createProduct(product));
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    //     }
    // }
}
