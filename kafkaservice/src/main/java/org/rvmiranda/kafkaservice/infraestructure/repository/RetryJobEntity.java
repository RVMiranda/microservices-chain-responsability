package org.rvmiranda.kafkaservice.infraestructure.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "products_retry_jobs", schema = "public")
public class RetryJobEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    // We can map jsonb to String for simplicity, though mapped native types require extra configuration or map as String and let Postgres cast it
    @Column(name = "request_data", columnDefinition = "jsonb")
    private String requestData;

    @Column(name = "response_data", columnDefinition = "jsonb")
    private String responseData;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "attempt", nullable = false)
    private Integer attempt;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "next_run_at", nullable = false)
    private OffsetDateTime nextRunAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
