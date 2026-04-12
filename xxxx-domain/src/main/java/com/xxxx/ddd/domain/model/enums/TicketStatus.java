package com.xxxx.ddd.domain.model.enums;

public enum TicketStatus {
    INACTIVE(0), ACTIVE(1), ENDED(2);

    private final int code;

    TicketStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static TicketStatus from(int code) {
        for (TicketStatus s : values()) {
            if (s.code == code)
                return s;
        }
        throw new IllegalArgumentException("Unknown ticket status: " + code);
    }
}
