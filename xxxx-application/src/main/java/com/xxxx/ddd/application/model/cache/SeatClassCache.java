package com.xxxx.ddd.application.model.cache;

import com.xxxx.ddd.domain.model.entity.SeatClass;

import lombok.Data;

@Data
public class SeatClassCache {

    private Long version;
    private SeatClass seatClass;

    public SeatClassCache withClone(SeatClass seatClass) {
        this.seatClass = seatClass;
        return this;
    }

    public SeatClassCache withVersion(Long version) {
        this.version = version;
        return this;
    }
}
