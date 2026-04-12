package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.Order;
import com.xxxx.ddd.domain.respository.OrderRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.OrderJPAMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderInfrasRepositoryImpl implements OrderRepository {

    private final OrderJPAMapper orderJPAMapper;

    @Override
    public Order save(Order order) {
        return orderJPAMapper.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJPAMapper.findById(id);
    }

    @Override
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderJPAMapper.findByUserId(userId, pageable);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return orderJPAMapper.existsByIdAndUserId(id, userId);
    }

}
