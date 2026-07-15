package com.order.service;

import com.order.entity.CreateOrderRequest;
import com.order.entity.Order;
import com.order.entity.OrderCreatedEvent;
import com.order.entity.OrderResponse;
import com.order.exceptions.InsufficientStockException;
import com.order.exceptions.OrderNotFoundException;
import com.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final OrderEventProducer eventProducer;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for product ID: {} with quantity: {}",
                request.productId(), request.quantity());

        // Check inventory
        var inventoryResponse = inventoryServiceClient.getStock(request.productId());

        if (inventoryResponse.availableQuantity() < request.quantity()) {
            log.warn("Insufficient stock for product ID: {}", request.productId());
            throw new InsufficientStockException(
                    "Insufficient stock for product ID: " + request.productId());
        }

        // Reserve stock
        inventoryServiceClient.reserveStock(request.productId(), request.quantity());

        // Create order
        String orderNumber = generateOrderNumber();
        BigDecimal totalPrice = calculateTotalPrice(request.quantity());

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .productId(request.productId())
                .quantity(request.quantity())
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", orderNumber);

        // Publish event to Kafka
        OrderCreatedEvent event = new OrderCreatedEvent(
                orderNumber,
                request.productId(),
                request.quantity(),
                totalPrice,
                System.currentTimeMillis()
        );
        eventProducer.publishOrderCreated(event);

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        log.debug("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByProduct(Long productId) {
        log.debug("Fetching orders for product ID: {}", productId);
        return orderRepository.findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        log.info("Updating order ID: {} to status: {}", id, status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated: {}", order.getOrderNumber());
        return mapToResponse(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);
        orderRepository.deleteById(id);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8);
    }

    private BigDecimal calculateTotalPrice(Integer quantity) {
        return BigDecimal.valueOf(100L * quantity);
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}