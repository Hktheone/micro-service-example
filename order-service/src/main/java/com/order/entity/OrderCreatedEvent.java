package com.order.entity;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        String orderNumber,
        Long productId,
        Integer quantity,
        BigDecimal totalPrice,
        long timestamp
) {}
