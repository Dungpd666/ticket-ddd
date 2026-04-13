package com.xxxx.ddd.application.service.seatClass;

import com.xxxx.ddd.application.model.SeatClassDTO;

public interface SeatClassAppService {

    SeatClassDTO getSeatClassById(Long seatClassId, Long version);

    boolean orderSeatClassByUser(Long seatClassId, Long userId, int quantity);
}
