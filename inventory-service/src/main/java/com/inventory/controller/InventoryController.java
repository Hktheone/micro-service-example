package com.inventory.controller;
import com.inventory.model.CreateInventoryRequest;
import com.inventory.model.InventoryResponse;
import com.inventory.model.StockCheckResponse;
import com.inventory.model.UpdateInventoryRequest;
import com.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody CreateInventoryRequest request) {
        log.info("POST /api/inventory - Creating new inventory");
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        log.info("GET /api/inventory/{}", productId);
        InventoryResponse response = inventoryService.getInventory(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/check")
    public ResponseEntity<StockCheckResponse> checkStock(
            @PathVariable Long productId,
            @RequestParam Integer requiredQuantity) {
        log.info("Checking stock for product: {}", productId);
        StockCheckResponse response = inventoryService.checkStock(productId, requiredQuantity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        log.info("POST /api/inventory/{}/reserve", productId);
        InventoryResponse response = inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        log.info("POST /api/inventory/{}/release", productId);
        InventoryResponse response = inventoryService.releaseStock(productId, quantity);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateInventoryRequest request) {
        log.info("PUT /api/inventory/{}", productId);
        InventoryResponse response = inventoryService.updateInventory(productId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventories() {
        log.info("GET /api/inventory - Fetching all inventories");
        List<InventoryResponse> response = inventoryService.getAllInventories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory Service is running");
    }
}