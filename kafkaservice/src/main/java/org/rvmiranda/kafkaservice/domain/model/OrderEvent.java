package org.rvmiranda.kafkaservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventId;
    private String orderId;
    private String action;
    private String status;
    private String requestData;
    private String responseData;
    private String errorDetails;
}
