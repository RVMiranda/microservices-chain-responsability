package org.rvmiranda.pagoservice.infrastructure.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String orderId;
    private String paymentMethod;
}
