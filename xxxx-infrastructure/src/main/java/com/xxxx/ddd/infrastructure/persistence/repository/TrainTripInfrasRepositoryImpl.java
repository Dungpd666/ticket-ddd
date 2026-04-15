package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.TrainTrip;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;
import com.xxxx.ddd.domain.respository.TrainTripRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.TrainTripJPAMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainTripInfrasRepositoryImpl implements TrainTripRepository {

    private final TrainTripJPAMapper trainTripJPAMapper;

    @Override
    public Optional<TrainTrip> findById(Long id) {
        return trainTripJPAMapper.findById(id);
    }

    @Override
    public Page<TrainTrip> findAll(Pageable pageable) {
        return trainTripJPAMapper.findAll(pageable);
    }

    @Override
    public Page<TrainTrip> findByStatus(TrainTripStatus status, Pageable pageable) {
        return trainTripJPAMapper.findByStatus(status, pageable);
    }

    @Override
    public Page<TrainTrip> search(String origin, String destination, Date dayStart, Date dayEnd, Pageable pageable) {
        return trainTripJPAMapper.searchTrips(origin, destination, dayStart, dayEnd, pageable);
    }
}
