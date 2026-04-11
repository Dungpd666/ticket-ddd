package com.xxxx.ddd.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

import com.xxxx.ddd.domain.model.enums.OrderStatus;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "OrderEntity")
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long ticketDetailId;
    private int quantity;

    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;

    private Date createdAt;
    private Date updatedAt;
}
