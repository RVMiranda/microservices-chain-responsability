package org.rvmiranda.pagoservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private String id;
    private String orderId;
    private String userEmail;
    private Double amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime processedAt;
}
