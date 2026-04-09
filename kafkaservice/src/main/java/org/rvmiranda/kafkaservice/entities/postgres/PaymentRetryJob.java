package org.rvmiranda.kafkaservice.entities.postgres;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments_retry_jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRetryJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_data")
    private JsonNode requestData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_data")
    private JsonNode responseData;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(nullable = false)
    private Integer attempt;

    @Column(nullable = false)
    private String status;

    @Column(name = "next_run_at", nullable = false)
    private OffsetDateTime nextRunAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void applyNextRunDefault() {
        if (nextRunAt == null) {
            nextRunAt = OffsetDateTime.now();
        }
    }
}
