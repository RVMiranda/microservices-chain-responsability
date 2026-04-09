package org.rvmiranda.kafkaservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.dto.ProductDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductConsumer {


    @KafkaListener(topics = "${kafka.topic.productos}", groupId = "${spring.kafka.consumer.group-id")
    public void eschucharProducto(String message) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            ProductDto product = mapper.readValue(message, ProductDto.class);
            log.info("producto recibido: nombre={}", product.getName());
        } catch (Exception e){
            log.error("error al recibir producto: {}", e.getMessage());
        }
    }
}
