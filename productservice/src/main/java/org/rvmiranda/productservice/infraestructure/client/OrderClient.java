package org.rvmiranda.productservice.infraestructure.client;

import org.rvmiranda.productservice.common.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ordenservice")
public interface OrderClient {
    @GetMapping("/ordenes/producto/{productId}/exists")
    GenericResponse<Boolean> existsOrderByProductId(@PathVariable("productId") String productId);
}
