package org.rvmiranda.kafkaservice.application.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.application.chain.AbstractRetryHandler;
import org.rvmiranda.kafkaservice.domain.RetryContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationHandler extends AbstractRetryHandler {

    @Override
    protected void execute(RetryContext context) {
        log.info("Paso B - EmailNotificationHandler: Preparando correo para el contexto: {}", context.getJobId());

        try {
            // Simulated Email Sending
            log.info("Simulando envío de correo a admin@empresa.com informando éxito para dominio {} con ID {}", 
                     context.getDomainType(), context.getEntityId());
            
            // Lógica ficticia para avanzar
            log.info("Paso B exitoso: Correo enviado de manera dummy (preparado para Spring Mail).");
        } catch (Exception e) {
            log.error("Paso B falló: Problema al enviar correo.", e);
            throw new RuntimeException("Fallo en paso B de la Cadena: Envío de emal fracasó.", e);
        }
    }
}
