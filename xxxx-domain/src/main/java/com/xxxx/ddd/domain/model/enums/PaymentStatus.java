package com.xxxx.ddd.domain.model.enums;

public enum PaymentStatus {
    PENDING(0), SUCCESS(1), FAILED(2);

    private final int code;

    PaymentStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static PaymentStatus from(int code) {
        for (PaymentStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown payment status: " + code);
    }
}
