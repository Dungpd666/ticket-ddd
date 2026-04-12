package com.xxxx.ddd.application.model;

import java.util.Date;

import com.xxxx.ddd.domain.model.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private Long ticketDetailId;
    private int quantity;
    private OrderStatus status;
    private Date createdAt;
}
