package org.rvmiranda.ordenservice.infraestructure.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String productId;
    private String productName;
    private Double totalPrice;
    private Integer quantity;
    private String userEmail;
}
