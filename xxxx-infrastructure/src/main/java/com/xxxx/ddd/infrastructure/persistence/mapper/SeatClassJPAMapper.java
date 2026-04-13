package com.xxxx.ddd.infrastructure.persistence.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ddd.domain.model.entity.SeatClass;

public interface SeatClassJPAMapper extends JpaRepository<SeatClass, Long> {
    Page<SeatClass> findByTripId(Long tripId, Pageable pageable);
}
