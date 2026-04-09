package org.rvmiranda.ordenservice.infraestructure.repository;

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
@Document(collection = "ordenes")
public class OrderEntity {
    @Id
    private String id;
    private String productId;
    private String productName;
    private String userEmail;
    private Integer quantity;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
}
