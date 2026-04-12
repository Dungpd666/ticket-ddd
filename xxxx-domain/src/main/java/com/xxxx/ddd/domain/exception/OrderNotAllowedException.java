package com.xxxx.ddd.domain.exception;

public class OrderNotAllowedException extends RuntimeException {
    public OrderNotAllowedException(String message) {
        super(message);
    }
}
