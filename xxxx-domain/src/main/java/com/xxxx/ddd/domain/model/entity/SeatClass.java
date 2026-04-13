package com.xxxx.ddd.domain.model.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seat_class")
public class SeatClass {

    @Id
    private Long id;
    private String name;
    private String description;
    private int stockInitial;
    private int stockAvailable;
    private boolean isStockPrepared;
    private Long price;
    private Long priceFlash;
    private Date saleStartTime;
    private Date saleEndTime;
    private int status;
    private Long tripId;
    private Date updatedAt;
    private Date createdAt;
}
