package com.xxxx.ddd.controller.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xxxx.ddd.application.model.SeatClassDTO;
import com.xxxx.ddd.application.model.TrainTripDTO;
import com.xxxx.ddd.application.service.trainTrip.TrainTripAppService;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.vo.PageResult;
import com.xxxx.ddd.controller.model.vo.ResultMessage;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trains")
@RequiredArgsConstructor
public class TrainTripController {

    private final TrainTripAppService trainTripAppService;

    @GetMapping("/{tripId}")
    public ResultMessage<TrainTripDTO> getTripById(@PathVariable Long tripId) {
        return ResultUtil.data(trainTripAppService.getTripById(tripId));
    }

    @GetMapping
    public ResultMessage<PageResult<TrainTripDTO>> listTrips(
            @RequestParam(required = false) TrainTripStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResultUtil.data(PageResult.of(trainTripAppService.listTrips(status, page, size)));
    }

    @GetMapping("/{tripId}/seat-classes")
    public ResultMessage<PageResult<SeatClassDTO>> listSeatClasses(
            @PathVariable Long tripId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResultUtil.data(PageResult.of(trainTripAppService.listSeatClasses(tripId, page, size)));
    }
}
