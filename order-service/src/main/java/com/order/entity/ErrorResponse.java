package com.order.entity;

public record ErrorResponse(
        int status,
        String message,
        long timestamp
) {}
