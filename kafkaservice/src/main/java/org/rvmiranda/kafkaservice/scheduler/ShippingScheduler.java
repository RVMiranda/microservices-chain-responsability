package org.rvmiranda.kafkaservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rvmiranda.kafkaservice.entities.postgres.Shipping;
import org.rvmiranda.kafkaservice.repository.postgres.ShippingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingScheduler {

    private final ShippingRepository shippingRepository;

    @Scheduled(fixedDelay = 10000) // Cada 10 segundos
    public void processPendingShipments() {
        List<Shipping> pendingShipments = shippingRepository.findByStatus("PENDING");
        
        if (!pendingShipments.isEmpty()) {
            log.info("Processing {} pending shipments...", pendingShipments.size());
            for (Shipping shipping : pendingShipments) {
                try {
                    // 1. Simular envío de correo
                    log.info(">>> Enviando correo de CONFIRMADO ENVIO DE ORDEN para la orden: {}", shipping.getOrderId());
                    
                    // 2. Actualizar estado
                    shipping.setStatus("SHIPPED");
                    shipping.setProcessedAt(OffsetDateTime.now());
                    shippingRepository.save(shipping);
                    
                    log.info("Shipment marked as SHIPPED for order: {}", shipping.getOrderId());
                } catch (Exception e) {
                    log.error("Error processing shipment for order: {}", shipping.getOrderId(), e);
                }
            }
        }
    }
}
