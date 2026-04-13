package com.xxxx.ddd.infrastructure.persistence.mapper;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ddd.domain.model.entity.Route;

public interface RouteJPAMapper extends JpaRepository<Route, Long> {
    List<Route> findByOriginStationId(Long originStationId);
}
