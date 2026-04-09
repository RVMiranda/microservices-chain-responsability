package org.rvmiranda.pagoservice.infrastructure.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pagos")
public class PaymentEntity {
    @Id
    private String id;
    private String orderId;
    private String userEmail;
    private Double amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime processedAt;
}
