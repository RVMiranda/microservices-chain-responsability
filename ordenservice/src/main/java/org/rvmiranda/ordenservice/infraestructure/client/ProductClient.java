package org.rvmiranda.ordenservice.infraestructure.client;

import org.rvmiranda.ordenservice.common.GenericResponse;
import org.rvmiranda.ordenservice.infraestructure.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "productservice")
public interface ProductClient {
    @GetMapping("/productos/{id}")
    GenericResponse<ProductDto> getProductById(@PathVariable("id") String id);

    @PutMapping("/productos/{id}/reduce-stock")
    GenericResponse<String> reduceStock(@PathVariable("id") String id, @RequestParam("quantity") Integer quantity);
}
