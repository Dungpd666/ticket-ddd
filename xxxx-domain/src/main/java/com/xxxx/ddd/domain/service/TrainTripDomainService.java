package com.xxxx.ddd.domain.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xxxx.ddd.domain.model.entity.SeatClass;
import com.xxxx.ddd.domain.model.entity.TrainTrip;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;

public interface TrainTripDomainService {
    Optional<TrainTrip> getTripById(Long id);

    Page<TrainTrip> listTrips(TrainTripStatus status, Pageable pageable);

    void validateOrderable(SeatClass seatClass, int quantity);
}
