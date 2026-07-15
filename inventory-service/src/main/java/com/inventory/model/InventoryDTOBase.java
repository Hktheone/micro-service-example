package com.inventory.model;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

public sealed interface InventoryDTOBase permits
        CreateInventoryRequest, UpdateInventoryRequest, InventoryResponse {}

