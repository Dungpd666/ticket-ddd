package com.xxxx.ddd.application.service.trainTrip;

import org.springframework.data.domain.Page;

import com.xxxx.ddd.application.model.SeatClassDTO;
import com.xxxx.ddd.application.model.TrainTripDTO;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;

public interface TrainTripAppService {
    TrainTripDTO getTripById(Long tripId);

    Page<TrainTripDTO> listTrips(TrainTripStatus status, int page, int size);

    Page<SeatClassDTO> listSeatClasses(Long tripId, int page, int size);
}
