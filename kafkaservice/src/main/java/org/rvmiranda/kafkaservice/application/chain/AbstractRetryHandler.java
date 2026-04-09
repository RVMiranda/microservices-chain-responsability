package org.rvmiranda.kafkaservice.application.chain;

import org.rvmiranda.kafkaservice.domain.RetryContext;

public abstract class AbstractRetryHandler implements RetryHandler {

    private RetryHandler nextHandler;

    @Override
    public void setNextHandler(RetryHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void process(RetryContext context) {
        // Execute logic specific to the handler
        execute(context);
        
        // Pass control to the next handler in the chain
        if (nextHandler != null) {
            nextHandler.process(context);
        }
    }

    protected abstract void execute(RetryContext context);
}
