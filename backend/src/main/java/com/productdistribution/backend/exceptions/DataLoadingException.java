package com.productdistribution.backend.exceptions;

public class DataLoadingException extends RuntimeException {

    public DataLoadingException(String message) {
        super(message);
    }

    public DataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}