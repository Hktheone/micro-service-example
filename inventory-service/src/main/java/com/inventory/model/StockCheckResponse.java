package com.inventory.model;

public record StockCheckResponse(
        Long productId,
        Integer availableQuantity,
        Boolean sufficient
) {}
