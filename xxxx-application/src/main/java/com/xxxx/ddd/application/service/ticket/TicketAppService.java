package com.xxxx.ddd.application.service.ticket;

import org.springframework.data.domain.Page;

import com.xxxx.ddd.application.model.TicketDTO;
import com.xxxx.ddd.application.model.TicketDetailDTO;
import com.xxxx.ddd.domain.model.enums.TicketStatus;

public interface TicketAppService {
    TicketDTO getTicketById(Long ticketId);

    Page<TicketDTO> listTickets(TicketStatus status, int page, int size);

    Page<TicketDetailDTO> listTicketDetails(Long ticketId, int page, int size);
}
