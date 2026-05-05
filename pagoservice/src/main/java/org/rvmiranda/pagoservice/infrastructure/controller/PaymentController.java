package org.rvmiranda.pagoservice.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.pagoservice.application.service.PaymentService;
import org.rvmiranda.pagoservice.common.GenericResponse;
import org.rvmiranda.pagoservice.domain.model.Payment;
import org.rvmiranda.pagoservice.infrastructure.dto.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /*@PostMapping
    public ResponseEntity<GenericResponse<Payment>> processPayment(@RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(request.getOrderId(), request.getPaymentMethod());
            return ResponseEntity.ok(GenericResponse.success(payment, "Pago procesado y orden actualizada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }*/

    @PostMapping("/procesar")
    public ResponseEntity<GenericResponse<Payment>> processPayment(@RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(
                    request.getOrderId(), 
                    request.getPaymentMethod(), 
                    request.getAmount(), 
                    request.getUserEmail()
            );

            // Publicar evento de éxito en tópico normal
            String requestDataJson = String.format("{\"orderId\":\"%s\", \"paymentMethod\":\"%s\", \"amount\":%s, \"userEmail\":\"%s\"}",
                    request.getOrderId(), request.getPaymentMethod(), request.getAmount(), request.getUserEmail());

            var successEvent = new PaymentEvent(
                    UUID.randomUUID().toString(),
                    payment.getId(),
                    "PROCESS",
                    "SUCCESS",
                    requestDataJson,
                    null,
                    null
            );
            kafkaTemplate.send("payment_received_events", successEvent);

            return ResponseEntity.ok(GenericResponse.success(payment, "Pago procesado y evento enviado asíncronamente"));
        } catch (Exception e) {
            String requestDataJson = String.format("{\"orderId\":\"%s\", \"paymentMethod\":\"%s\"}",
                    request.getOrderId(), request.getPaymentMethod());

            var failedEvent = new PaymentEvent(
                    UUID.randomUUID().toString(),
                    "unknown-payment-id",
                    "PROCESS",
                    "FAILED",
                    requestDataJson,
                    null,
                    e.getMessage()
            );
            kafkaTemplate.send("payment-events-retry", failedEvent);

            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Error al procesar el pago, evento de reintento enviado a Kafka: " + e.getMessage())
            );
        }
    }

    // El GET general de todos los pagos
    @GetMapping
    public ResponseEntity<GenericResponse<List<Payment>>> getAllPayments() {
        return ResponseEntity.ok(GenericResponse.success(paymentService.getAllPayments(), "Historial de pagos obtenido"));
    }

    // GET /pagos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<Payment>> getPaymentById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(GenericResponse.success(paymentService.getPaymentById(id), "Detalle del pago"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }

    // GET /pagos/orden/{id}
    @GetMapping("/orden/{id}")
    public ResponseEntity<GenericResponse<Payment>> getPaymentByOrderId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(GenericResponse.success(paymentService.getPaymentByOrderId(id), "Pago correspondiente a la orden"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(GenericResponse.error(e.getMessage()));
        }
    }

    // PUT /pagos/{id}/reembolso
    @PutMapping("/{id}/reembolso")
    public ResponseEntity<GenericResponse<Payment>> refundPayment(@PathVariable String id) {
        try {
            return ResponseEntity.ok(GenericResponse.success(paymentService.refundPayment(id), "Reembolso procesado exitosamente"));
        } catch (Exception e) {
            String requestDataJson = String.format("{\"action\":\"REFUND\"}");

            var failedEvent = new PaymentEvent(
                    UUID.randomUUID().toString(),
                    id,
                    "REFUND",
                    "FAILED",
                    requestDataJson,
                    null,
                    e.getMessage()
            );
            kafkaTemplate.send("payment-events-retry", failedEvent);

            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Error al procesar el reembolso, evento de reintento enviado a Kafka: " + e.getMessage())
            );
        }
    }

    // ── Test: simula un fallo de pago y publica el evento a Kafka ─────────────
    @PostMapping("/test-failure")
    public ResponseEntity<GenericResponse<String>> testFailure() {
        try {
            throw new RuntimeException("Simulated error processing payment in external system");
        } catch (RuntimeException ex) {
            var failedEvent = new PaymentEvent(
                    UUID.randomUUID().toString(),
                    "fake-test-payment-id",
                    "PROCESS",
                    "FAILED",
                    "{\"orderId\": \"order-001\", \"paymentMethod\": \"CREDIT_CARD\"}",
                    null,
                    ex.getMessage()
            );
            kafkaTemplate.send("payment-events-retry", failedEvent);
            return ResponseEntity.badRequest().body(
                    GenericResponse.error("Pago fallido simulado, evento enviado a Kafka.")
            );
        }
    }

    public record PaymentEvent(
            String eventId,
            String paymentId,
            String action,
            String status,
            String requestData,
            String responseData,
            String errorDetails
    ) {}
}
