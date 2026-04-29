package org.rvmiranda.kafkaservice.infraestructure.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    // Mapped with @JdbcTypeCode so Hibernate sends the value as a proper jsonb PGobject
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_data")
    private String requestData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_data")
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
