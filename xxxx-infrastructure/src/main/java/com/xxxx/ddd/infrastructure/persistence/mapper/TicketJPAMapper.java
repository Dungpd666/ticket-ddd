package com.xxxx.ddd.infrastructure.persistence.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ddd.domain.model.entity.Ticket;
import com.xxxx.ddd.domain.model.enums.TicketStatus;

public interface TicketJPAMapper extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
}
