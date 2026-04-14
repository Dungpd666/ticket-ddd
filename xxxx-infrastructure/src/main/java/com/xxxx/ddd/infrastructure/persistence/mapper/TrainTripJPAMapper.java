package com.xxxx.ddd.infrastructure.persistence.mapper;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xxxx.ddd.domain.model.entity.TrainTrip;
import com.xxxx.ddd.domain.model.enums.TrainTripStatus;

import org.springframework.data.repository.query.Param;

public interface TrainTripJPAMapper extends JpaRepository<TrainTrip, Long> {
    Page<TrainTrip> findByStatus(TrainTripStatus status, Pageable pageable);

    @Query("SELECT t FROM TrainTrip t WHERE t.origin = :origin " +
            "AND t.destination = :destination " +
            "AND t.departureTime >= :dayStart " +
            "AND t.departureTime < :dayEnd " +
            "AND t.status = com.xxxx.ddd.domain.model.enums.TrainTripStatus.ACTIVE")
    Page<TrainTrip> searchTrips(@Param("origin") String origin,
            @Param("destination") String destination,
            @Param("dayStart") Date dayStart,
            @Param("dayEnd") Date dayEnd,
            Pageable pageable);
}
