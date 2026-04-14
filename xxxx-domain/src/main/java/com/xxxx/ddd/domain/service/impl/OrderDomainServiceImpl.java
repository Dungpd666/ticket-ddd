package com.xxxx.ddd.domain.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xxxx.ddd.domain.exception.OrderNotAllowedException;
import com.xxxx.ddd.domain.exception.OrderNotFoundException;
import com.xxxx.ddd.domain.model.entity.Order;
import com.xxxx.ddd.domain.model.enums.OrderStatus;
import com.xxxx.ddd.domain.respository.OrderRepository;
import com.xxxx.ddd.domain.service.OrderDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderDomainServiceImpl implements OrderDomainService {

    private final OrderRepository orderRepository;

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Page<Order> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderNotAllowedException("Order already cancelled");
        }
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            throw new OrderNotAllowedException("Order already completed, cannot cancel");
        }

        order.setStatus(OrderStatus.CANCELLED).setUpdatedAt(new Date());
        return orderRepository.save(order);
    }

}
