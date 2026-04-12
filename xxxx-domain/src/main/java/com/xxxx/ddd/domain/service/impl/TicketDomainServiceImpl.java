package com.xxxx.ddd.domain.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.Ticket;
import com.xxxx.ddd.domain.model.enums.TicketStatus;
import com.xxxx.ddd.domain.respository.TicketRepository;
import com.xxxx.ddd.domain.service.TicketDomainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketDomainServiceImpl implements TicketDomainService {
    private final TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Page<Ticket> listTickets(TicketStatus status, Pageable pageable) {
        if (status != null) {
            return ticketRepository.findByStatus(status, pageable);
        } else {
            return ticketRepository.findAll(pageable);
        }
    }

}
