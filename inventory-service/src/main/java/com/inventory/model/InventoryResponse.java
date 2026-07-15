package com.inventory.model;

import java.time.LocalDateTime;

public record InventoryResponse(
        Long id,
        Long productId,
        String productName,
        Integer totalQuantity,
        Integer availableQuantity,
        Integer reservedQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements InventoryDTOBase {}
