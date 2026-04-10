package com.xxxx.ddd.domain.service.impl;

import java.util.Date;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.TicketDetail;
import com.xxxx.ddd.domain.respository.TicketDetailRepository;
import com.xxxx.ddd.domain.service.TicketDetailDomainService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketDetailDomainServiceImpl implements TicketDetailDomainService {
    @Autowired
    private TicketDetailRepository ticketDetailRepository;

    @Override
    public TicketDetail getTicketDetailById(Long ticketId) {
        return ticketDetailRepository.findById(ticketId).orElse(null);
    }

    @Override
    @Transactional
    public boolean decrementStock(Long ticketId) {
        TicketDetail ticketDetail = ticketDetailRepository.findById(ticketId).orElse(null);
        if (ticketDetail == null) {
            log.warn("TicketDetail not found for id: {}", ticketId);
            return false;
        }

        ticketDetail.setStockAvailable(ticketDetail.getStockAvailable() - 1);
        ticketDetail.setUpdatedAt(new Date());
        ticketDetailRepository.save(ticketDetail);
        return true;
    }
}
