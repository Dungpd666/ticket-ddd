package com.xxxx.ddd.domain.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xxxx.ddd.domain.model.entity.SeatClass;

public interface SeatClassRepository {

    Optional<SeatClass> findById(Long id);

    SeatClass save(SeatClass seatClass);

    Page<SeatClass> findByTripId(Long tripId, Pageable pageable);
}
