package com.order.service;

public record InventoryResponse(
        Long productId,
        Integer availableQuantity,
        Long lastUpdated,
        String message
) {}
