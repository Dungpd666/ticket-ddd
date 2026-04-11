package com.xxxx.ddd.application.service.order;

import org.springframework.data.domain.Page;

import com.xxxx.ddd.application.model.OrderDTO;

public interface OrderAppService {

    OrderDTO getOrderById(Long id, Long userId);

    Page<OrderDTO> getOrdersByUserId(Long userId, int page, int size);

    OrderDTO cancelOrder(Long orderId);

}
