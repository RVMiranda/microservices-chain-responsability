package org.rvmiranda.kafkaservice.api;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.kafkaservice.dto.ProductDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

@Repository
@RequiredArgsConstructor
public class AlmacenApi {
    private final WebClient almacenWebClient;

    public Object crearProductoAlmacen(ProductDto product) {
        return almacenWebClient.post()
                .uri("/create-prduct-almacen")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}
