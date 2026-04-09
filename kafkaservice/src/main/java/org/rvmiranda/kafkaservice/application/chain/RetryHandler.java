package org.rvmiranda.kafkaservice.application.chain;

import org.rvmiranda.kafkaservice.domain.RetryContext;

public interface RetryHandler {
    void process(RetryContext context);
    void setNextHandler(RetryHandler nextHandler);
}
