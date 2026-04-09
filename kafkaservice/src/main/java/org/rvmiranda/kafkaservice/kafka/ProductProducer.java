package org.rvmiranda.kafkaservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.dto.ProductDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductProducer {
    @Value("${kafka.topic.productos}")
    private String topic;

    private final KafkaTemplate<String, ProductDto> kafkaTemplate;

    public void enviarProducto(ProductDto product) {
        CompletableFuture<SendResult<String, ProductDto>> future =
                kafkaTemplate.send(topic, product.getName(), product);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Error al enviar producto [{}]: {}", product.getName(), ex.getMessage());
            } else {
                log.info("Producto enviado [{}] -> partition: {}, offset: {}",
                        product.getName(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
