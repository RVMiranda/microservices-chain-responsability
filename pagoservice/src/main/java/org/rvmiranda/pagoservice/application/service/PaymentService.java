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

    public Payment processPayment(String orderId, String paymentMethod) {
        OrderDto order;

        // 1. Preguntar por la orden
        try {
            GenericResponse<OrderDto> response = orderClient.getOrderById(orderId);
            order = response.getData();
        } catch (FeignException e) {
            throw new RuntimeException("Error: La orden " + orderId + " no existe.");
        }

        // 2. Validar que no esté pagada o cancelada
        if (!"CREADA".equals(order.getStatus())) {
            throw new RuntimeException("Error: La orden ya fue procesada (Estado actual: " + order.getStatus() + ")");
        }

        // 3. Crear el registro del pago usando el precio que nos dio la orden
        Payment newPayment = Payment.builder()
                .orderId(orderId)
                .userEmail(order.getUserEmail())
                .amount(order.getTotalPrice())
                .paymentMethod(paymentMethod)
                .status("EXITOSO")
                .processedAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepositoryPort.save(newPayment);

        // 4. Avisarle a la orden que ya cobramos
        orderClient.updateOrderStatus(orderId, "PAGADA");

        return savedPayment;
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

        // 4. ¡Magia! Le avisamos a la orden que la compra se echó para atrás
        orderClient.updateOrderStatus(payment.getOrderId(), "REEMBOLSADA");

        return updatedPayment;
    }
}
