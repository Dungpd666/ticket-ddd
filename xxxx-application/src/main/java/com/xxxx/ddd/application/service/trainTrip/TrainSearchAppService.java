package com.xxxx.ddd.application.service.trainTrip;

import org.springframework.data.domain.Page;

import com.xxxx.ddd.application.model.TrainTripDTO;

public interface TrainSearchAppService {

    Page<TrainTripDTO> searchTrips(String origin, String destination, String date, int page, int size);
}
