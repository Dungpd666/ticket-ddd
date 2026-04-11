package com.xxxx.ddd.application.model;

import java.util.Date;

import lombok.Data;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private Long ticketDetailId;
    private int quantity;
    private int status; // 0 = pending, 1 = completed, 2 = cancelled
    private Date createdAt;
}
