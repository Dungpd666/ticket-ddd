package com.xxxx.ddd.application.mapper;

import org.springframework.beans.BeanUtils;

import com.xxxx.ddd.application.model.TrainTripDTO;
import com.xxxx.ddd.domain.model.entity.TrainTrip;

public class TrainTripMapper {
    public static TrainTripDTO toDTO(TrainTrip trainTrip) {
        if (trainTrip == null) {
            return null;
        }
        TrainTripDTO dto = new TrainTripDTO();
        BeanUtils.copyProperties(trainTrip, dto);
        return dto;
    }
}
