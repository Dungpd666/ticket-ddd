package com.xxxx.ddd.domain.model.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.xxxx.ddd.domain.model.enums.TrainTripStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "train_trip")
public class TrainTrip {

    @Id
    private Long id;

    private String name;

    @Column(name = "desc")
    private String description;

    private Date departureTime;
    private Date arrivalTime;

    @Enumerated(EnumType.ORDINAL)
    private TrainTripStatus status;

    private Long routeId;
    private Long trainId;
    private String origin;
    private String destination;

    private Date updatedAt;
    private Date createdAt;
}
