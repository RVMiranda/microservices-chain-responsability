package org.rvmiranda.ordenservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderBalance {
    private String id;
    private String orderId;
    private Double remainingBalance;
    private LocalDateTime updatedAt;
}
