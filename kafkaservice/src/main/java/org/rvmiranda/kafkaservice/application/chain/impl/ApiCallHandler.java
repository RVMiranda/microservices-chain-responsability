package org.rvmiranda.kafkaservice.application.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.application.chain.AbstractRetryHandler;
import org.rvmiranda.kafkaservice.domain.RetryContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ApiCallHandler extends AbstractRetryHandler {

    private final RestTemplate restTemplate;

    public ApiCallHandler() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    protected void execute(RetryContext context) {
        log.info("Paso A - ApiCallHandler: Iniciando reenvío para el contexto: {}", context.getJobId());

        String targetUrl = determineTargetUrl(context.getDomainType());
        
        try {
            // Simulated REST call
            log.info("Simulando llamada a API {} con el payload {}", targetUrl, context.getRequestData());
            // ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, context.getRequestData(), String.class);
            
            // Lógica ficticia para avanzar
            log.info("Paso A exitoso: Endpoint notificado correctamente.");
        } catch (Exception e) {
            log.error("Paso A falló: Problema al llamar al endpoint en ApiCallHandler.", e);
            throw new RuntimeException("Fallo en paso A de la Cadena: Llama HTTP fracasó.", e);
        }
    }

    private String determineTargetUrl(String domainType) {
        return switch (domainType) {
            case "PAYMENT" -> "http://api-gateway/payments/retry";
            case "ORDER" -> "http://api-gateway/orders/retry";
            case "PRODUCT" -> "http://api-gateway/products/retry";
            default -> "http://api-gateway/unknown";
        };
    }
}
