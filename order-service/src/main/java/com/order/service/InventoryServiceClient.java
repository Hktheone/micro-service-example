package com.order.service;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "inventory-service",
        url = "${inventory.service.url:http://localhost:8082}")
public interface InventoryServiceClient {

    @GetMapping("/api/inventory/{productId}")
    @CircuitBreaker(name = "inventory-service",
            fallbackMethod = "getStockFallback")
    InventoryResponse getStock(@PathVariable Long productId);

    @PostMapping("/api/inventory/{productId}/reserve")
    @CircuitBreaker(name = "inventory-service",
            fallbackMethod = "reserveStockFallback")
    InventoryResponse reserveStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity
    );

    default InventoryResponse getStockFallback(Long productId, Exception ex) {
        return new InventoryResponse(productId, 0, null, "Service unavailable");
    }

    default InventoryResponse reserveStockFallback(Long productId, Integer quantity, Exception ex) {
        return new InventoryResponse(productId, quantity, null, "Reservation failed");
    }
}

