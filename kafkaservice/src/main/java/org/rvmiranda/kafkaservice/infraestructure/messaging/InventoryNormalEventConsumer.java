package org.rvmiranda.kafkaservice.infraestructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.domain.model.OrderEvent;
import org.rvmiranda.kafkaservice.entities.postgres.ProductHistory;
import org.rvmiranda.kafkaservice.repository.postgres.ProductHistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryNormalEventConsumer {

    private final ProductHistoryRepository productHistoryRepository;

    @KafkaListener(topics = "inventory_update_events", groupId = "inventory-normal-listener", containerFactory = "orderEventKafkaListenerContainerFactory")
    public void consume(OrderEvent event) {
        log.info("Received Normal InventoryEvent: {}", event);
        try {
            // Guardar en ProductHistory
            // Usamos OrderEvent porque en el product controller emitimos ProductEvent y en Order emitimos OrderEvent.
            // Para simplificar, la clase POJO asimila los atributos básicos (eventId, action, status, requestData)
            ProductHistory history = ProductHistory.builder()
                    .productId(event.getOrderId() != null ? event.getOrderId() : "N/A") // El ID del producto viene en orderId cuando usamos ProductEvent
                    .action(event.getAction())
                    .details(event.getRequestData())
                    .createdAt(OffsetDateTime.now())
                    .build();
            productHistoryRepository.save(history);
            log.info("Saved ProductHistory for action: {}", event.getAction());

            // Actualizar inventario ya se hace en productservice, kafkaservice solo guarda historial.

        } catch (Exception e) {
            log.error("Error processing Normal InventoryEvent: {}", e.getMessage(), e);
        }
    }
}
