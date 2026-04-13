package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.SeatClass;
import com.xxxx.ddd.domain.respository.SeatClassRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.SeatClassJPAMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatClassInfrasRepositoryImpl implements SeatClassRepository {

    private final SeatClassJPAMapper seatClassJPAMapper;

    @Override
    public Optional<SeatClass> findById(Long id) {
        return seatClassJPAMapper.findById(id);
    }

    @Override
    public SeatClass save(SeatClass seatClass) {
        return seatClassJPAMapper.save(seatClass);
    }

    @Override
    public Page<SeatClass> findByTripId(Long tripId, Pageable pageable) {
        return seatClassJPAMapper.findByTripId(tripId, pageable);
    }
}
