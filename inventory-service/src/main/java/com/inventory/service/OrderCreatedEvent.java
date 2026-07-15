package com.inventory.service;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        String orderNumber,
        Long productId,
        Integer quantity,
        BigDecimal totalPrice,
        long timestamp
) {}
