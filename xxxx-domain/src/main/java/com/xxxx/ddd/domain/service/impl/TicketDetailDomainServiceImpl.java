package com.xxxx.ddd.domain.service.impl;

import java.util.Date;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.exception.OrderNotAllowedException;
import com.xxxx.ddd.domain.model.entity.TicketDetail;
import com.xxxx.ddd.domain.respository.TicketDetailRepository;
import com.xxxx.ddd.domain.service.TicketDetailDomainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailDomainServiceImpl implements TicketDetailDomainService {
    private final TicketDetailRepository ticketDetailRepository;

    @Override
    public TicketDetail getTicketDetailById(Long ticketId) {
        return ticketDetailRepository.findById(ticketId).orElse(null);
    }

    @Override
    @Transactional
    public boolean decrementStock(Long ticketId, int quantity) {
        TicketDetail ticketDetail = ticketDetailRepository.findById(ticketId).orElse(null);
        if (ticketDetail == null) {
            throw new OrderNotAllowedException("Ticket not found: " + ticketId);
        }
        if (ticketDetail.getStockAvailable() < quantity) {
            throw new OrderNotAllowedException(
                "Not enough stock: requested " + quantity + ", available " + ticketDetail.getStockAvailable()
            );
        }

        ticketDetail.setStockAvailable(ticketDetail.getStockAvailable() - quantity);
        ticketDetail.setUpdatedAt(new Date());
        ticketDetailRepository.save(ticketDetail);
        return true;
    }

    @Override
    @Transactional
    public boolean incrementStock(Long ticketId, int quantity) {
        TicketDetail ticketDetail = ticketDetailRepository.findById(ticketId).orElse(null);
        if (ticketDetail == null) {
            log.warn("TicketDetail not found for id: {}", ticketId);
            return false;
        }

        ticketDetail.setStockAvailable(ticketDetail.getStockAvailable() + quantity);
        ticketDetail.setUpdatedAt(new Date());
        ticketDetailRepository.save(ticketDetail);
        return true;
    }

    @Override
    public Page<TicketDetail> getTicketDetailById(Long ticketId, Pageable pageable) {
        return ticketDetailRepository.findByActivityId(ticketId, pageable);
    }
}
