package com.xxxx.ddd.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xxxx.ddd.domain.model.entity.TicketDetail;

public interface TicketDetailDomainService {

    TicketDetail getTicketDetailById(Long ticketId);

    boolean decrementStock(Long ticketId, int quantity);

    boolean incrementStock(Long ticketId, int quantity);

    public Page<TicketDetail> getTicketDetailById(Long ticketId, Pageable pageable);
}
