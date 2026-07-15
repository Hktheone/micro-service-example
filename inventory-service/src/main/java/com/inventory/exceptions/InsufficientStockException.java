package com.inventory.exceptions;

public class InsufficientStockException extends ApplicationException {
    public InsufficientStockException(String message) {
        super(message);
    }
    @Override
    public int getHttpStatus() {
        return 400;
    }
}
