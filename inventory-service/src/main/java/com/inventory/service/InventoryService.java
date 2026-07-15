package com.inventory.service;

import com.inventory.exceptions.InsufficientStockException;
import com.inventory.exceptions.InventoryNotFoundException;
import com.inventory.model.*;
import com.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse createInventory(CreateInventoryRequest request) {
        log.info("Creating inventory for product ID: {}", request.productId());

        if (inventoryRepository.findByProductId(request.productId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Inventory already exists for product ID: " + request.productId());
        }

        ProductInventory inventory = ProductInventory.builder()
                .productId(request.productId())
                .productName(request.productName())
                .totalQuantity(request.totalQuantity())
                .availableQuantity(request.totalQuantity())
                .reservedQuantity(0)
                .build();

        ProductInventory savedInventory = inventoryRepository.save(inventory);
        log.info("Inventory created for product ID: {}", request.productId());

        return mapToResponse(savedInventory);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {
        log.debug("Fetching inventory for product ID: {}", productId);
        ProductInventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId));
        return mapToResponse(inventory);
    }

    @Transactional(readOnly = true)
    public StockCheckResponse checkStock(Long productId, Integer requiredQuantity) {
        log.debug("Checking stock for product ID: {}", productId);
        ProductInventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId));

        boolean sufficient = inventory.getAvailableQuantity() >= requiredQuantity;
        return new StockCheckResponse(productId, inventory.getAvailableQuantity(), sufficient);
    }

    @Transactional
    public InventoryResponse reserveStock(Long productId, Integer quantity) {
        log.info("Reserving {} units for product ID: {}", quantity, productId);

        ProductInventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product ID: " + productId);
        }

        inventory.reserve(quantity);
        ProductInventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Stock reserved successfully for product ID: {}", productId);
        return mapToResponse(updatedInventory);
    }

    @Transactional
    public InventoryResponse releaseStock(Long productId, Integer quantity) {
        log.info("Releasing {} reserved units for product ID: {}", quantity, productId);

        ProductInventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId));

        inventory.release(quantity);
        ProductInventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Stock released successfully");
        return mapToResponse(updatedInventory);
    }

    @Transactional
    public InventoryResponse updateInventory(Long productId, UpdateInventoryRequest request) {
        log.info("Updating inventory for product ID: {}", productId);

        ProductInventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "Inventory not found for product ID: " + productId));

        inventory.setTotalQuantity(request.totalQuantity());
        ProductInventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Inventory updated");
        return mapToResponse(updatedInventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventories() {
        log.debug("Fetching all inventories");
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private InventoryResponse mapToResponse(ProductInventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getProductName(),
                inventory.getTotalQuantity(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}