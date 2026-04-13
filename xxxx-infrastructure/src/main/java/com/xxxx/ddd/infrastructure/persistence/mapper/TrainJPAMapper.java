package com.xxxx.ddd.infrastructure.persistence.mapper;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ddd.domain.model.entity.Train;

public interface TrainJPAMapper extends JpaRepository<Train, Long> {
    Optional<Train> findByTrainNumber(String trainNumber);
}
