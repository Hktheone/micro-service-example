package com.inventory.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateInventoryRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "Product name is required")
        String productName,

        @NotNull(message = "Total quantity is required")
        @Positive(message = "Total quantity must be positive")
        Integer totalQuantity
) implements InventoryDTOBase {}
