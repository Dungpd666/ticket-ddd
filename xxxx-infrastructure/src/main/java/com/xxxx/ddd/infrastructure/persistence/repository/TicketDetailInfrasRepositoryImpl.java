package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.TicketDetail;
import com.xxxx.ddd.domain.respository.TicketDetailRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.TicketDetailJPAMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailInfrasRepositoryImpl implements TicketDetailRepository {

    private final TicketDetailJPAMapper ticketDetailJPAMapper;

    @Override
    public Optional<TicketDetail> findById(Long id) {
        return ticketDetailJPAMapper.findById(id);
    }

    @Override
    public TicketDetail save(TicketDetail ticketDetail) {
        return ticketDetailJPAMapper.save(ticketDetail);
    }

    @Override
    public Page<TicketDetail> findByActivityId(Long ticketId, Pageable pageable) {
        return ticketDetailJPAMapper.findByActivityId(ticketId, pageable);
    }
}
