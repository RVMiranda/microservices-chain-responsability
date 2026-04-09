package org.rvmiranda.productservice.infraestructure.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "productos")
public class ProductEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
}
