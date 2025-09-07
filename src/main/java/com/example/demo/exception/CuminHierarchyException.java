package com.example.demo.exception;

public class CuminHierarchyException extends RuntimeException {
    private final String errorCode;

    public CuminHierarchyException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
