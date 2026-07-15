package com.inventory.exceptions;


public abstract class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
    public abstract int getHttpStatus();
}

