package org.rvmiranda.pagoservice.application.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.rvmiranda.pagoservice.common.GenericResponse;
import org.rvmiranda.pagoservice.domain.model.Payment;
import org.rvmiranda.pagoservice.domain.port.PaymentRepositoryPort;

import org.rvmiranda.pagoservice.infrastructure.client.OrderClient;
import org.rvmiranda.pagoservice.infrastructure.dto.OrderDto;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final OrderClient orderClient;

    public Payment processPayment(String orderId, String paymentMethod, Double amount, String userEmail) {
        // Validación de monto
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Error: El monto del pago debe ser mayor a 0");
        }

        // Consultar la orden
        GenericResponse<OrderDto> orderResponse;
        try {
            orderResponse = orderClient.getOrderById(orderId);
        } catch (FeignException e) {
            throw new RuntimeException("Error al consultar la orden: " + e.getMessage());
        }

        if (orderResponse == null || orderResponse.getData() == null) {
            throw new RuntimeException("Error: Orden no encontrada");
        }

        OrderDto order = orderResponse.getData();

        // Validar que la orden no esté CANCELADA.
        // Si está REEMBOLSADA se permite pagar de nuevo (regla de negocio solicitada).
        if ("CANCELADA".equals(order.getStatus())) {
            throw new RuntimeException("Error: No se puede pagar una orden CANCELADA.");
        }

        // Sumar pagos previos exitosos
        List<Payment> previousPayments = paymentRepositoryPort.findAllByOrderId(orderId);
        double totalPaidSoFar = previousPayments.stream()
                .filter(p -> "EXITOSO".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        if (totalPaidSoFar + amount > order.getTotalPrice()) {
            throw new RuntimeException(String.format("Error: El monto excede el total de la orden. Total orden: %s, Pagado: %s, Intento: %s",
                    order.getTotalPrice(), totalPaidSoFar, amount));
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

    public boolean isOrderFullyPaid(String orderId) {
        OrderDto order = orderClient.getOrderById(orderId).getData();
        if (order == null) return false;

        List<Payment> previousPayments = paymentRepositoryPort.findAllByOrderId(orderId);
        double totalPaidSoFar = previousPayments.stream()
                .filter(p -> "EXITOSO".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        // Consideramos completamente pagado si la suma es igual o mayor al total
        return totalPaidSoFar >= order.getTotalPrice();
    }

    public Double getRemainingBalance(String orderId) {
        OrderDto order = orderClient.getOrderById(orderId).getData();
        if (order == null) return 0.0;

        List<Payment> previousPayments = paymentRepositoryPort.findAllByOrderId(orderId);
        double totalPaidSoFar = previousPayments.stream()
                .filter(p -> "EXITOSO".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        return Math.max(0.0, order.getTotalPrice() - totalPaidSoFar);
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
