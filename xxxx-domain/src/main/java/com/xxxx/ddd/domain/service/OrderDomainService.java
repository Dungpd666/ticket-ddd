package com.xxxx.ddd.domain.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xxxx.ddd.domain.model.entity.Order;

public interface OrderDomainService {
    Optional<Order> getOrderById(Long orderId);

    Page<Order> getOrdersByUserId(Long userId, Pageable pageable);

    Order cancelOrder(Long orderId);
}
