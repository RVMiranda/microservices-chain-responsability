package org.rvmiranda.kafkaservice.controller;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.kafkaservice.dto.GenericResponse;
import org.rvmiranda.kafkaservice.entities.postgres.Shipping;
import org.rvmiranda.kafkaservice.repository.postgres.ShippingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingRepository shippingRepository;

    @GetMapping
    public ResponseEntity<GenericResponse<List<Shipping>>> getAllShipments() {
        try {
            List<Shipping> list = shippingRepository.findAll();
            return ResponseEntity.ok(GenericResponse.success(list, "Historial de envíos obtenido con éxito"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponse.error("Error al obtener envíos: " + e.getMessage()));
        }
    }

    @GetMapping("/orden/{orderId}")
    public ResponseEntity<GenericResponse<List<Shipping>>> getShipmentByOrderId(@PathVariable String orderId) {
        try {
            List<Shipping> list = shippingRepository.findByOrderId(orderId)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
            return ResponseEntity.ok(GenericResponse.success(list, "Envío de la orden obtenido con éxito"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponse.error("Error al obtener envío: " + e.getMessage()));
        }
    }
}
