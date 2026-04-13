package com.xxxx.ddd.application.model;

import java.util.Date;

import com.xxxx.ddd.domain.model.enums.TrainTripStatus;

import lombok.Data;

@Data
public class TrainTripDTO {
    private Long id;
    private String name;
    private String description;
    private Date departureTime;
    private Date arrivalTime;
    private TrainTripStatus status;
    private Long routeId;
    private Long trainId;
    private String origin;
    private String destination;
    private Date createdAt;
}
