package com.xxxx.ddd.domain.respository;

import com.xxxx.ddd.domain.model.entity.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);
}
