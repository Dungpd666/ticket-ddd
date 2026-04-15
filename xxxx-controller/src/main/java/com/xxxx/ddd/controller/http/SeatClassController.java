package com.xxxx.ddd.controller.http;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xxxx.ddd.application.model.OrderDTO;
import com.xxxx.ddd.application.model.SeatClassDTO;
import com.xxxx.ddd.application.service.seatClass.SeatClassAppService;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.request.OrderRequest;
import com.xxxx.ddd.controller.model.vo.ResultMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/trains/{tripId}/seat-classes")
@Slf4j
@RequiredArgsConstructor
public class SeatClassController {

    private final SeatClassAppService seatClassAppService;

    @GetMapping("/{seatClassId}")
    public ResultMessage<SeatClassDTO> getSeatClassById(
            @PathVariable Long tripId,
            @PathVariable Long seatClassId,
            @RequestParam(required = false) Long version) {
        return ResultUtil.data(seatClassAppService.getSeatClassById(seatClassId, version));
    }

    @PostMapping("/{seatClassId}/book")
    public ResultMessage<OrderDTO> bookSeatClass(
            @PathVariable Long tripId,
            @PathVariable Long seatClassId,
            @RequestBody @Valid OrderRequest request) {

        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        OrderDTO order = seatClassAppService.orderSeatClassByUser(seatClassId, userId,
                request.getQuantity());
        return ResultUtil.data(order);
    }
}
