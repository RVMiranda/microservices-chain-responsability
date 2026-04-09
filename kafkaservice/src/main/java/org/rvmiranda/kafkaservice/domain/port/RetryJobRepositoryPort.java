package org.rvmiranda.kafkaservice.domain.port;

import org.rvmiranda.kafkaservice.domain.model.RetryJob;

public interface RetryJobRepositoryPort {
    RetryJob save(RetryJob retryJob);
}
