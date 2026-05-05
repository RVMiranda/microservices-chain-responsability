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
@Table(name = "shipping")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;
    
    @Column(nullable = false)
    private String status; // e.g. PENDING, SHIPPED
    
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    
    @Column(name = "processed_at")
    private OffsetDateTime processedAt;
}
