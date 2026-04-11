package com.xxxx.ddd.infrastructure.persistence.mapper;

import com.xxxx.ddd.domain.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJPAMapper extends JpaRepository<Order, Long> {
}
