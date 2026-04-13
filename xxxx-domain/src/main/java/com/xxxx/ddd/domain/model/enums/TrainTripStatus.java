package com.xxxx.ddd.domain.model.enums;

public enum TrainTripStatus {
    INACTIVE(0), ACTIVE(1), ENDED(2);

    private final int code;

    TrainTripStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static TrainTripStatus from(int code) {
        for (TrainTripStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown train trip status: " + code);
    }
}
