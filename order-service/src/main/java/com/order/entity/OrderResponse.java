package com.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long productId,
        Integer quantity,
        BigDecimal totalPrice,
        Order.OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements OrderDTOBase {}
