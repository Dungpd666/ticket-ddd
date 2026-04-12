package com.xxxx.ddd.domain.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xxxx.ddd.domain.model.entity.TicketDetail;

public interface TicketDetailRepository {

    Optional<TicketDetail> findById(Long id);

    TicketDetail save(TicketDetail ticketDetail);

    Page<TicketDetail> findByActivityId(Long ticketId, Pageable pageable);
}
