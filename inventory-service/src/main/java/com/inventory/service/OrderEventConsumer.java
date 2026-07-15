package com.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created-topic", groupId = "inventory-service-group")
    public void consumeOrderCreated(String message) {
        try {
            log.info("Received order event: {}", message);
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            log.info("Processing order for product: {} with quantity: {}",
                    event.productId(), event.quantity());
            // Here you can add logic to update stock, generate shipment, etc.
        } catch (Exception e) {
            log.error("Error processing order event", e);
        }
    }
}

