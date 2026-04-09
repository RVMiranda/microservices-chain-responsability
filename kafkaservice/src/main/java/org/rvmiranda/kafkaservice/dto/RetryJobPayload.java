package org.rvmiranda.kafkaservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetryJobPayload {
    private JsonNode data;
    private StepResult sendEmail;
    private StepResult updateRetryJobs;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepResult {
        private String status;
        private String message;
    }
}
