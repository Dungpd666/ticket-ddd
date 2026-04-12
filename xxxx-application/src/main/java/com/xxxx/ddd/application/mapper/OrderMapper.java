package com.xxxx.ddd.application.mapper;

import org.springframework.beans.BeanUtils;

import com.xxxx.ddd.application.model.OrderDTO;
import com.xxxx.ddd.domain.model.entity.Order;

public class OrderMapper {

    public static OrderDTO mapperToOrderDTO(Order order) {
        if (order == null) return null;

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);
        return orderDTO;
    }
}
