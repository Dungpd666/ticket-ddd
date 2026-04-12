package com.xxxx.ddd.application.service.ticketDetail;

import com.xxxx.ddd.application.model.TicketDetailDTO;

public interface TicketDetailAppService {

    TicketDetailDTO getTicketDetailById(Long ticketId, Long version);

    boolean orderTicketByUser(Long ticketId, Long userId, int quantity);
}
