package com.xxxx.ddd.domain.respository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.xxxx.ddd.domain.model.entity.TrainTrip;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;

@Repository
public interface TrainTripRepository {
    Optional<TrainTrip> findById(Long id);

    Page<TrainTrip> findAll(Pageable pageable);

    Page<TrainTrip> findByStatus(TrainTripStatus status, Pageable pageable);

    Page<TrainTrip> search(String origin, String destination, Date dayStart, Date dayEnd, Pageable pageable);
}
