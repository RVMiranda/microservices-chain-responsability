package org.rvmiranda.kafkaservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class AlmacenConfig {

    @Bean
    public WebClient almacenWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api/almacen")
                .build();
    }
}
