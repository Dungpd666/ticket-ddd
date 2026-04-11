package com.xxxx.ddd.domain.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class OrderTest {

    @Test
    void should_build_order_with_chain() {
        Date now = new Date();

        Order order = new Order()
                .setUserId(123L)
                .setTicketDetailId(456L)
                .setQuantity(2)
                .setStatus(0)
                .setCreatedAt(now);

        assertThat(order.getUserId()).isEqualTo(123L);
        assertThat(order.getTicketDetailId()).isEqualTo(456L);
        assertThat(order.getQuantity()).isEqualTo(2);
        assertThat(order.getStatus()).isEqualTo(0);
    }

    @Test
    void status_0_means_pending() {
        Order order = new Order().setStatus(0);
        assertThat(order.getStatus()).isEqualTo(0);
    }

    @Test
    void status_1_means_completed() {
        Order order = new Order().setStatus(1);
        assertThat(order.getStatus()).isEqualTo(1);
    }

    @Test
    void status_2_means_cancelled() {
        Order order = new Order().setStatus(2);
        assertThat(order.getStatus()).isEqualTo(2);
    }
}
