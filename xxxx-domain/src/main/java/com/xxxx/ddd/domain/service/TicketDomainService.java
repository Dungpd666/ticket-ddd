package com.xxxx.ddd.domain.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xxxx.ddd.domain.model.entity.Ticket;
import com.xxxx.ddd.domain.model.entity.TicketDetail;
import com.xxxx.ddd.domain.model.enums.TicketStatus;

public interface TicketDomainService {
    Optional<Ticket> getTicketById(Long id);

    Page<Ticket> listTickets(TicketStatus status, Pageable pageable);

    void validateOrderable(TicketDetail ticketDetail, int quantity);
}
