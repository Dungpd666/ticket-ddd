package com.xxxx.ddd.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xxxx.ddd.domain.model.entity.SeatClass;

public interface SeatClassDomainService {

    SeatClass getSeatClassById(Long id);

    boolean decrementStock(Long seatClassId, int quantity);

    boolean incrementStock(Long seatClassId, int quantity);

    Page<SeatClass> getSeatClassesByTripId(Long tripId, Pageable pageable);
}
