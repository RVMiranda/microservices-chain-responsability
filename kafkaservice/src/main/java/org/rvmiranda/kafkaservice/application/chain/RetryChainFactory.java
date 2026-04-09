package org.rvmiranda.kafkaservice.application.chain;

import lombok.RequiredArgsConstructor;
import org.rvmiranda.kafkaservice.application.chain.impl.ApiCallHandler;
import org.rvmiranda.kafkaservice.application.chain.impl.EmailNotificationHandler;
import org.rvmiranda.kafkaservice.application.chain.impl.UpdateDatabaseHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetryChainFactory {

    private final ApiCallHandler stepA_ApiCall;
    private final EmailNotificationHandler stepB_Email;
    private final UpdateDatabaseHandler stepC_UpdateDb;

    public RetryHandler buildChain() {
        // Enlazar la cadena: A -> B -> C
        stepA_ApiCall.setNextHandler(stepB_Email);
        stepB_Email.setNextHandler(stepC_UpdateDb);
        // paso C es el último, no tiene next

        return stepA_ApiCall; // Se devuelve el inicio de la cadena
    }
}
