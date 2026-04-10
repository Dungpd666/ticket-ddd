package com.xxxx.ddd.infrastructure.persistence.repository;

import com.xxxx.ddd.domain.model.entity.Order;
import com.xxxx.ddd.domain.respository.OrderRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.OrderJPAMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderInfrasRepositoryImpl implements OrderRepository {

    private OrderJPAMapper orderJPAMapper;

    @Override
    public Order save(Order order) {
        return orderJPAMapper.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJPAMapper.findById(id);
    }
}
