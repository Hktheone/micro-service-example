package com.inventory.exceptions;

public class InventoryNotFoundException extends ApplicationException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
    @Override
    public int getHttpStatus() {
        return 404;
    }
}
