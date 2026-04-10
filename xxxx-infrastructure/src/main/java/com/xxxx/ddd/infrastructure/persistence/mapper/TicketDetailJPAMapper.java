package com.xxxx.ddd.infrastructure.persistence.mapper;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ddd.domain.model.entity.TicketDetail;

public interface TicketDetailJPAMapper extends JpaRepository<TicketDetail, Long> {

    Optional<TicketDetail> findById(Long id);

}
