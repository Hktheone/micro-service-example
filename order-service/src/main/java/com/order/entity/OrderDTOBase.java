package com.order.entity;

public sealed interface OrderDTOBase permits CreateOrderRequest, OrderResponse {}

