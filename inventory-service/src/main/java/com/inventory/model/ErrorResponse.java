package com.inventory.model;

public record ErrorResponse(
        int status,
        String message,
        long timestamp
) {}
