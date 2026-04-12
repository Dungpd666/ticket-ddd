package com.xxxx.ddd.infrastructure.persistence.mapper;

import com.xxxx.ddd.domain.model.entity.Order;
import com.xxxx.ddd.domain.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderJPAMapperTest {

    @Autowired
    private OrderJPAMapper orderJPAMapper;

    @Test
    void save_and_findById_should_work() {
        Order order = new Order()
                .setUserId(1L)
                .setTicketDetailId(10L)
                .setQuantity(2)
                .setStatus(OrderStatus.PENDING)
                .setCreatedAt(new Date())
                .setUpdatedAt(new Date());

        Order saved = orderJPAMapper.save(order);

        assertThat(saved.getId()).isNotNull(); // auto-generated ID

        Optional<Order> found = orderJPAMapper.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(1L);
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void findById_should_return_empty_for_missing_id() {
        Optional<Order> found = orderJPAMapper.findById(9999L);
        assertThat(found).isEmpty();
    }
}
