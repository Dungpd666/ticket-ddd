package com.xxxx.ddd.application.service.trainTrip.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.application.mapper.TrainTripMapper;
import com.xxxx.ddd.application.model.TrainTripDTO;
import com.xxxx.ddd.application.service.trainTrip.TrainSearchAppService;
import com.xxxx.ddd.domain.respository.TrainTripRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainSearchAppServiceImpl implements TrainSearchAppService {

    private final TrainTripRepository trainTripRepository;

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    @Override
    public Page<TrainTripDTO> searchTrips(String origin, String destination, String date, int page, int size) {
        LocalDate localDate = LocalDate.parse(date);
        Date dayStart = Date.from(localDate.atStartOfDay(VIETNAM_ZONE).toInstant());
        Date dayEnd = Date.from(localDate.plusDays(1).atStartOfDay(VIETNAM_ZONE).toInstant());

        return trainTripRepository.search(origin, destination, dayStart, dayEnd, PageRequest.of(page, size))
                .map(TrainTripMapper::toDTO);
    }
}
