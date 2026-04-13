package com.xxxx.ddd.application.model;

import java.util.Date;

import lombok.Data;

@Data
public class SeatClassDTO {
    private Long id;
    private String name;
    private int stockInitial;
    private int stockAvailable;
    private boolean isStockPrepared;
    private Long price;
    private Long priceFlash;
    private Date saleStartTime;
    private Date saleEndTime;
    private int status;
    private Long tripId;
    private Long version;
}
