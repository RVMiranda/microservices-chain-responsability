package org.rvmiranda.ordenservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String id;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double totalPrice;
    private String status;
    private String userEmail;
    private LocalDateTime createdAt;
}
