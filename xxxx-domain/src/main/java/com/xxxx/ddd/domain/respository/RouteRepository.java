package com.xxxx.ddd.domain.respository;

import java.util.List;
import java.util.Optional;

import com.xxxx.ddd.domain.model.entity.Route;

public interface RouteRepository {
    Optional<Route> findById(Long id);

    List<Route> findByOriginStationId(Long originStationId);

    Route save(Route route);
}
