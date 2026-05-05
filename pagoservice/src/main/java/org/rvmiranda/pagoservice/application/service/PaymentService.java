package org.rvmiranda.pagoservice.application.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.rvmiranda.pagoservice.common.GenericResponse;
import org.rvmiranda.pagoservice.domain.model.Payment;
import org.rvmiranda.pagoservice.domain.port.PaymentRepositoryPort;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepositoryPort paymentRepositoryPort;

    public Payment processPayment(String orderId, String paymentMethod, Double amount, String userEmail) {
        // Validación de monto
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Error: El monto del pago debe ser mayor a 0");
        }

        // Crear el registro del pago
        Payment newPayment = Payment.builder()
                .orderId(orderId)
                .userEmail(userEmail)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .status("EXITOSO")
                .processedAt(LocalDateTime.now())
                .build();

        return paymentRepositoryPort.save(newPayment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepositoryPort.findAll();
    }

    public Payment getPaymentById(String id) {
        return paymentRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Pago no encontrado"));
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepositoryPort.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Error: No hay pago registrado para la orden " + orderId));
    }

    public Payment refundPayment(String id) {
        // 1. Buscamos el pago
        Payment payment = getPaymentById(id);

        // 2. Validamos que no esté reembolsado ya
        if ("REEMBOLSADO".equals(payment.getStatus())) {
            throw new RuntimeException("Error: Este pago ya fue reembolsado");
        }

        // 3. Cambiamos el estado y guardamos
        payment.setStatus("REEMBOLSADO");
        Payment updatedPayment = paymentRepositoryPort.save(payment);

        // NOTA: La notificación a la orden ahora se hará mediante eventos asíncronos en el controlador.

        return updatedPayment;
    }
}
