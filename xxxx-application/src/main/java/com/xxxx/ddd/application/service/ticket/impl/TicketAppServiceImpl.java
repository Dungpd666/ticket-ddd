package com.xxxx.ddd.application.service.ticket.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.application.mapper.TicketDetailMapper;
import com.xxxx.ddd.application.mapper.TicketMapper;
import com.xxxx.ddd.application.model.TicketDTO;
import com.xxxx.ddd.application.model.TicketDetailDTO;
import com.xxxx.ddd.application.service.ticket.TicketAppService;
import com.xxxx.ddd.domain.model.enums.TicketStatus;
import com.xxxx.ddd.domain.service.TicketDetailDomainService;
import com.xxxx.ddd.domain.service.TicketDomainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketAppServiceImpl implements TicketAppService {

    private final TicketDomainService ticketDomainService;
    private final TicketDetailDomainService tickerDetailDomainService;

    @Override
    public TicketDTO getTicketById(Long ticketId) {
        return ticketDomainService.getTicketById(ticketId)
                .map(TicketMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
    }

    @Override
    public Page<TicketDTO> listTickets(TicketStatus status, int page, int size) {
        return ticketDomainService.listTickets(status, PageRequest.of(page, size))
                .map(TicketMapper::toDTO);
    }

    @Override
    public Page<TicketDetailDTO> listTicketDetails(Long ticketId, int page, int size) {
        return tickerDetailDomainService.getTicketDetailById(ticketId, PageRequest.of(page, size))
                .map(TicketDetailMapper::mapperToTicketDetailDTO);
    }
}
