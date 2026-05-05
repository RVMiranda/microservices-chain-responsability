package org.rvmiranda.kafkaservice.entities.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_history")
public class ProductHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "product_id", nullable = false)
    private String productId;
    
    @Column(nullable = false)
    private String action;
    
    @Column(columnDefinition = "text")
    private String details;
    
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
