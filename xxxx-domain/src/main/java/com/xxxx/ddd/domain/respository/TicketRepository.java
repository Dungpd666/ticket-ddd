package com.xxxx.ddd.domain.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.xxxx.ddd.domain.model.entity.Ticket;
import com.xxxx.ddd.domain.model.enums.TicketStatus;

@Repository
public interface TicketRepository {
    Optional<Ticket> findById(Long id);

    Page<Ticket> findAll(Pageable pageable);

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
}
