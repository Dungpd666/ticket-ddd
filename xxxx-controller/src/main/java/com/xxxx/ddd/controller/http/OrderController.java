package com.xxxx.ddd.controller.http;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xxxx.ddd.application.model.OrderDTO;
import com.xxxx.ddd.application.service.order.OrderAppService;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.vo.PageResult;
import com.xxxx.ddd.controller.model.vo.ResultMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderAppService orderAppService;

    @GetMapping("/{orderId}")
    public ResultMessage<OrderDTO> getOrderById(@PathVariable Long orderId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResultUtil.data(orderAppService.getOrderById(orderId, userId));
    }

    @GetMapping("/user/{userId}")
    public ResultMessage<PageResult<OrderDTO>> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResultUtil.data(PageResult.of(orderAppService.getOrdersByUserId(userId, page, size)));
    }

    @DeleteMapping("/{orderId}")
    public ResultMessage<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResultUtil.data(orderAppService.cancelOrder(orderId, userId));
    }
}
