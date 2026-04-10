package com.xxxx.ddd.domain.respository;

import java.util.Optional;

import com.xxxx.ddd.domain.model.entity.TicketDetail;

public interface TicketDetailRepository {

    Optional<TicketDetail> findById(Long id);

    TicketDetail save(TicketDetail ticketDetail);
}
