package org.rvmiranda.ordenservice.infraestructure.dto;

import lombok.Data;

@Data
public class ProductDto {
    private String id;
    private String name;
    private Double price;
    private Integer stock;
}
