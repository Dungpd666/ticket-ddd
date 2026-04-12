package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.Ticket;
import com.xxxx.ddd.domain.model.enums.TicketStatus;
import com.xxxx.ddd.domain.respository.TicketRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.TicketJPAMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketInfrasRepositoryImpl implements TicketRepository {
    private final TicketJPAMapper ticketJPAMapper;

    @Override
    public Optional<Ticket> findById(Long id) {
        return ticketJPAMapper.findById(id);
    }

    @Override
    public Page<Ticket> findAll(Pageable pageable) {
        return ticketJPAMapper.findAll(pageable);
    }

    @Override
    public Page<Ticket> findByStatus(TicketStatus status, Pageable pageable) {
        return ticketJPAMapper.findByStatus(status, pageable);
    }
}
