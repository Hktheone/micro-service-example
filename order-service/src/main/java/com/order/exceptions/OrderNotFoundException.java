package com.order.exceptions;

public class OrderNotFoundException extends ApplicationException {
    public OrderNotFoundException(String message) {
        super(message);
    }
    @Override
    public int getHttpStatus() {
        return 404;
    }
}
