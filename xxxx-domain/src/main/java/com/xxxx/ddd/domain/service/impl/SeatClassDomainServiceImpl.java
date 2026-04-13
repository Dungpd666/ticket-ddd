package com.xxxx.ddd.domain.service.impl;

import java.util.Date;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.exception.OrderNotAllowedException;
import com.xxxx.ddd.domain.model.entity.SeatClass;
import com.xxxx.ddd.domain.respository.SeatClassRepository;
import com.xxxx.ddd.domain.service.SeatClassDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatClassDomainServiceImpl implements SeatClassDomainService {

    private final SeatClassRepository seatClassRepository;

    @Override
    public SeatClass getSeatClassById(Long id) {
        return seatClassRepository.findById(id).orElse(null);
    }

    @Override
    public boolean decrementStock(Long seatClassId, int quantity) {
        SeatClass seatClass = seatClassRepository.findById(seatClassId).orElse(null);
        if (seatClass == null) {
            throw new OrderNotAllowedException("Seat class not found: " + seatClassId);
        }
        if (seatClass.getStockAvailable() < quantity) {
            throw new OrderNotAllowedException(
                    "Not enough stock: requested " + quantity + ", available " + seatClass.getStockAvailable());
        }
        seatClass.setStockAvailable(seatClass.getStockAvailable() - quantity);
        seatClass.setUpdatedAt(new Date());
        seatClassRepository.save(seatClass);
        return true;
    }

    @Override
    @Transactional
    public boolean incrementStock(Long seatClassId, int quantity) {
        SeatClass seatClass = seatClassRepository.findById(seatClassId).orElse(null);
        if (seatClass == null) {
            log.warn("SeatClass not found for id: {}", seatClassId);
            return false;
        }
        seatClass.setStockAvailable(seatClass.getStockAvailable() + quantity);
        seatClass.setUpdatedAt(new Date());
        seatClassRepository.save(seatClass);
        return true;
    }

    @Override
    public Page<SeatClass> getSeatClassesByTripId(Long tripId, Pageable pageable) {
        return seatClassRepository.findByTripId(tripId, pageable);
    }
}
