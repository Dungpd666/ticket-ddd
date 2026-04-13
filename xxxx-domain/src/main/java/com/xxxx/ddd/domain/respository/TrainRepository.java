package com.xxxx.ddd.domain.respository;

import java.util.Optional;

import com.xxxx.ddd.domain.model.entity.Train;

public interface TrainRepository {
    Optional<Train> findById(Long id);

    Optional<Train> findByTrainNumber(String trainNumber);

    Train save(Train train);
}
