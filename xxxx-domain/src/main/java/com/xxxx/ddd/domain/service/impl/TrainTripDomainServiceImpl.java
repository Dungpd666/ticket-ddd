package com.xxxx.ddd.domain.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.exception.OrderNotAllowedException;
import com.xxxx.ddd.domain.model.entity.SeatClass;
import com.xxxx.ddd.domain.model.entity.TrainTrip;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;
import com.xxxx.ddd.domain.respository.TrainTripRepository;
import com.xxxx.ddd.domain.service.TrainTripDomainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainTripDomainServiceImpl implements TrainTripDomainService {

    private final TrainTripRepository trainTripRepository;

    @Override
    public Optional<TrainTrip> getTripById(Long id) {
        return trainTripRepository.findById(id);
    }

    @Override
    public Page<TrainTrip> listTrips(TrainTripStatus status, Pageable pageable) {
        if (status != null) {
            return trainTripRepository.findByStatus(status, pageable);
        }
        return trainTripRepository.findAll(pageable);
    }

    @Override
    public void validateOrderable(SeatClass seatClass, int quantity) {
        if (seatClass == null) {
            throw new OrderNotAllowedException("Seat class not found");
        }
        if (seatClass.getStockAvailable() < quantity) {
            throw new OrderNotAllowedException("Not enough stock available");
        }
    }
}
