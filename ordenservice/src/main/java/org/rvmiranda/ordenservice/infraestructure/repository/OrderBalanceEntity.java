package org.rvmiranda.ordenservice.infraestructure.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "order_balances")
public class OrderBalanceEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String orderId;

    private Double remainingBalance;
    private LocalDateTime updatedAt;
}
