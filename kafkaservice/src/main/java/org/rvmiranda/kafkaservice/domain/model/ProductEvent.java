package org.rvmiranda.kafkaservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private String eventId;
    private String productId;
    private String action;
    private String status;
    private String requestData; // Can hold a JSON payload representing the product
    private String responseData;
    private String errorDetails;
}
