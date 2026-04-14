package com.xxxx.ddd.application.service.seatClass.impl;

import org.springframework.stereotype.Service;

import com.xxxx.ddd.application.mapper.OrderMapper;
import com.xxxx.ddd.application.mapper.SeatClassMapper;
import com.xxxx.ddd.application.model.OrderDTO;
import com.xxxx.ddd.application.model.SeatClassDTO;
import com.xxxx.ddd.application.model.cache.SeatClassCache;
import com.xxxx.ddd.application.service.seatClass.SeatClassAppService;
import com.xxxx.ddd.application.service.seatClass.cache.SeatClassCacheService;
import com.xxxx.ddd.domain.model.entity.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatClassAppServiceImpl implements SeatClassAppService {

    private final SeatClassCacheService seatClassCacheService;

    @Override
    public SeatClassDTO getSeatClassById(Long seatClassId, Long version) {
        log.info("getSeatClassById: id={}, version={}", seatClassId, version);
        SeatClassCache cache = seatClassCacheService.getSeatClass(seatClassId, version);
        SeatClassDTO dto = SeatClassMapper.toDTO(cache.getSeatClass());
        dto.setVersion(cache.getVersion());
        return dto;
    }

    @Override
    public OrderDTO orderSeatClassByUser(Long seatClassId, Long userId, int quantity) {
        Order order = seatClassCacheService.orderSeatClassByUser(seatClassId, userId, quantity);
        return OrderMapper.mapperToOrderDTO(order);
    }
}
