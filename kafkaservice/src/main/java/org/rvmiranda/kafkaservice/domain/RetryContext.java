package org.rvmiranda.kafkaservice.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RetryContext {
    private UUID jobId;          // ID en la base de datos
    private String domainType;   // "PAYMENT", "ORDER", "PRODUCT"
    private String entityId;     // paymentId, orderId, productId original
    private JsonNode requestData;
    private JsonNode responseData;
    private int currentAttempt;
}
