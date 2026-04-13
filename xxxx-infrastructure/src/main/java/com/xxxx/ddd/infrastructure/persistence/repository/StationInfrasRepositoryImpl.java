package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.Station;
import com.xxxx.ddd.domain.respository.StationRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.StationJPAMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StationInfrasRepositoryImpl implements StationRepository {

    private final StationJPAMapper stationJPAMapper;

    @Override
    public Optional<Station> findById(Long id) {
        return stationJPAMapper.findById(id);
    }

    @Override
    public Optional<Station> findByCode(String code) {
        return stationJPAMapper.findByCode(code);
    }

    @Override
    public List<Station> findAll() {
        return stationJPAMapper.findAll();
    }

    @Override
    public Station save(Station station) {
        return stationJPAMapper.save(station);
    }
}
