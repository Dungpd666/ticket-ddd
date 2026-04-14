package com.xxxx.ddd.application.service.order.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.application.mapper.OrderMapper;
import com.xxxx.ddd.application.model.OrderDTO;
import com.xxxx.ddd.application.service.order.OrderAppService;
import com.xxxx.ddd.domain.model.entity.Order;
import com.xxxx.ddd.domain.model.enums.OrderStatus;
import com.xxxx.ddd.domain.service.OrderDomainService;
import com.xxxx.ddd.domain.service.SeatClassDomainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderAppServiceImpl implements OrderAppService {

    private final OrderDomainService orderDomainService;
    private final SeatClassDomainService seatClassDomainService;

    @Override
    public OrderDTO getOrderById(Long id, Long userId) {
        Order order = orderDomainService.getOrderById(id)
                .filter(o -> o.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderMapper.mapperToOrderDTO(order);
    }

    @Override
    public Page<OrderDTO> getOrdersByUserId(Long userId, int page, int size) {
        return orderDomainService.getOrdersByUserId(userId, PageRequest.of(page, size))
                .map(OrderMapper::mapperToOrderDTO);
    }

    @Override
    public OrderDTO cancelOrder(Long orderId, Long userId) {
        orderDomainService.getOrderById(orderId)
                .filter(o -> o.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Order order = orderDomainService.cancelOrder(orderId);
        if (order.getStatus() == OrderStatus.PAYMENT_PENDING || order.getStatus() == OrderStatus.PENDING) {
            seatClassDomainService.incrementStock(order.getTicketDetailId(), order.getQuantity());
        }
        return OrderMapper.mapperToOrderDTO(order);
    }

}
