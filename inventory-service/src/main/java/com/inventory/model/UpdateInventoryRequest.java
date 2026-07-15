package com.inventory.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateInventoryRequest(
        @NotNull(message = "Total quantity is required")
        @Positive(message = "Total quantity must be positive")
        Integer totalQuantity
) implements InventoryDTOBase {}
