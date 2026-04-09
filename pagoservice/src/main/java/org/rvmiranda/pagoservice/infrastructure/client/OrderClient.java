package org.rvmiranda.pagoservice.infrastructure.client;

import org.rvmiranda.pagoservice.common.GenericResponse;
import org.rvmiranda.pagoservice.infrastructure.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ordenservice")
public interface OrderClient {
    // Para consultar cuánto cobrar
    @GetMapping("/ordenes/{id}")
    GenericResponse<OrderDto> getOrderById(@PathVariable("id") String id);

    // Para avisar que ya se pagó
    @PutMapping("/ordenes/{id}/update-status")
    GenericResponse<String> updateOrderStatus(@PathVariable("id") String id, @RequestParam("status") String status);
}
