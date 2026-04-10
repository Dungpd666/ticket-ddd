package com.xxxx.ddd.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.xxxx.ddd.domain.model.entity.Order;
import com.xxxx.ddd.infrastructure.persistence.mapper.OrderJPAMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderInfrasRepositoryImplTest {

    @Mock
    private OrderJPAMapper orderJPAMapper;

    @InjectMocks
    private OrderInfrasRepositoryImpl orderInfrasRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order()
                .setId(1L)
                .setUserId(123L)
                .setTicketDetailId(456L)
                .setQuantity(2)
                .setStatus(0);
    }

    @Test
    void save_should_delegate_to_jpa_mapper() {
        when(orderJPAMapper.save(order)).thenReturn(order);

        Order result = orderInfrasRepository.save(order);

        assertThat(result).isEqualTo(order);
        verify(orderJPAMapper, times(1)).save(order);
    }

    @Test
    void findById_should_return_order_when_exists() {
        when(orderJPAMapper.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderInfrasRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(123L);
    }

    @Test
    void findById_should_return_empty_when_not_exists() {
        when(orderJPAMapper.findById(99L)).thenReturn(Optional.empty());

        Optional<Order> result = orderInfrasRepository.findById(99L);

        assertThat(result).isEmpty();
    }
}
