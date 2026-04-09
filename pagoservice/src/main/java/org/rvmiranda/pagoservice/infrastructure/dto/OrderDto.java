package org.rvmiranda.pagoservice.infrastructure.dto;

import lombok.Data;

@Data
public class OrderDto {
    private String id;
    private String userEmail;
    private Double totalPrice;
    private String status;
}
