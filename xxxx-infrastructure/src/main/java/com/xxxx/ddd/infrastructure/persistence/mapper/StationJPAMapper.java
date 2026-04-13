package com.xxxx.ddd.infrastructure.persistence.mapper;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ddd.domain.model.entity.Station;

public interface StationJPAMapper extends JpaRepository<Station, Long> {
    Optional<Station> findByCode(String code);
}
