package com.xxxx.ddd.infrastructure.persistence.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ddd.domain.model.entity.TrainTrip;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;

public interface TrainTripJPAMapper extends JpaRepository<TrainTrip, Long> {
    Page<TrainTrip> findByStatus(TrainTripStatus status, Pageable pageable);
}
