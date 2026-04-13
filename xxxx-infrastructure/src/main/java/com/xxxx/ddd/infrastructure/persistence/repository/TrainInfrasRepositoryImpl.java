package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.Train;
import com.xxxx.ddd.domain.respository.TrainRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.TrainJPAMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainInfrasRepositoryImpl implements TrainRepository {

    private final TrainJPAMapper trainJPAMapper;

    @Override
    public Optional<Train> findById(Long id) {
        return trainJPAMapper.findById(id);
    }

    @Override
    public Optional<Train> findByTrainNumber(String trainNumber) {
        return trainJPAMapper.findByTrainNumber(trainNumber);
    }

    @Override
    public Train save(Train train) {
        return trainJPAMapper.save(train);
    }
}
