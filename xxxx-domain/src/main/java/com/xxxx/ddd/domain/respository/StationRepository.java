package com.xxxx.ddd.domain.respository;

import java.util.List;
import java.util.Optional;

import com.xxxx.ddd.domain.model.entity.Station;

public interface StationRepository {
    Optional<Station> findById(Long id);

    Optional<Station> findByCode(String code);

    List<Station> findAll();

    Station save(Station station);
}
