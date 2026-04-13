package com.xxxx.ddd.application.mapper;

import org.springframework.beans.BeanUtils;

import com.xxxx.ddd.application.model.SeatClassDTO;
import com.xxxx.ddd.domain.model.entity.SeatClass;

public class SeatClassMapper {
    public static SeatClassDTO toDTO(SeatClass seatClass) {
        if (seatClass == null) {
            return null;
        }
        SeatClassDTO dto = new SeatClassDTO();
        BeanUtils.copyProperties(seatClass, dto);
        return dto;
    }
}
