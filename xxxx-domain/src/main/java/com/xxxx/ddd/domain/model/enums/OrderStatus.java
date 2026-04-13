package com.xxxx.ddd.domain.model.enums;

public enum OrderStatus {
    PENDING(0), CONFIRMED(1), CANCELLED(2), PAYMENT_PENDING(3), FAILED(4);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static OrderStatus from(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown order status: " + code);
    }
}
