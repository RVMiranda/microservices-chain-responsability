package org.rvmiranda.kafkaservice.domain.model;

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
public class RetryJob {
    private UUID id;
    private String productId;
    private String requestData;
    private String responseData;
    private String action;
    private Integer attempt;
    private String status;
    private OffsetDateTime nextRunAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
