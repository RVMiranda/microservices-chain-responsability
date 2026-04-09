package org.rvmiranda.kafkaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String name;
    private String description;
    private Double price;
    //private Integer quantity;
    //private String image;
    //private String category;
    //private String subcategory;
    //private String brand;
    //private String supp;
}
