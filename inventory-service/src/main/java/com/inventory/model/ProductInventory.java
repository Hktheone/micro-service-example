package com.inventory.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (reservedQuantity == null) reservedQuantity = 0;
        if (availableQuantity == null) availableQuantity = totalQuantity;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void reserve(Integer quantity) {
        if (availableQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }

    public void release(Integer quantity) {
        if (reservedQuantity < quantity) {
            throw new IllegalArgumentException("Cannot release more than reserved");
        }
        reservedQuantity -= quantity;
        availableQuantity += quantity;
    }
}