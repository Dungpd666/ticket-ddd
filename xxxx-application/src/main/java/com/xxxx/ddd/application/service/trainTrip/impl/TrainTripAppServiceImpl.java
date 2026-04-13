package com.xxxx.ddd.application.service.trainTrip.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.application.mapper.SeatClassMapper;
import com.xxxx.ddd.application.mapper.TrainTripMapper;
import com.xxxx.ddd.application.model.SeatClassDTO;
import com.xxxx.ddd.application.model.TrainTripDTO;
import com.xxxx.ddd.application.service.trainTrip.TrainTripAppService;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;
import com.xxxx.ddd.domain.service.SeatClassDomainService;
import com.xxxx.ddd.domain.service.TrainTripDomainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainTripAppServiceImpl implements TrainTripAppService {

    private final TrainTripDomainService trainTripDomainService;
    private final SeatClassDomainService seatClassDomainService;

    @Override
    public TrainTripDTO getTripById(Long tripId) {
        return trainTripDomainService.getTripById(tripId)
                .map(TrainTripMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Train trip not found with id: " + tripId));
    }

    @Override
    public Page<TrainTripDTO> listTrips(TrainTripStatus status, int page, int size) {
        return trainTripDomainService.listTrips(status, PageRequest.of(page, size))
                .map(TrainTripMapper::toDTO);
    }

    @Override
    public Page<SeatClassDTO> listSeatClasses(Long tripId, int page, int size) {
        return seatClassDomainService.getSeatClassesByTripId(tripId, PageRequest.of(page, size))
                .map(SeatClassMapper::toDTO);
    }
}
