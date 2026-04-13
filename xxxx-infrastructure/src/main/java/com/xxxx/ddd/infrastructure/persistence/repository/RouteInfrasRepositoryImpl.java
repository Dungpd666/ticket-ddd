package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.Route;
import com.xxxx.ddd.domain.respository.RouteRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.RouteJPAMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteInfrasRepositoryImpl implements RouteRepository {

    private final RouteJPAMapper routeJPAMapper;

    @Override
    public Optional<Route> findById(Long id) {
        return routeJPAMapper.findById(id);
    }

    @Override
    public List<Route> findByOriginStationId(Long originStationId) {
        return routeJPAMapper.findByOriginStationId(originStationId);
    }

    @Override
    public Route save(Route route) {
        return routeJPAMapper.save(route);
    }
}
