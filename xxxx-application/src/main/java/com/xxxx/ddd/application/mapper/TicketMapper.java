package com.xxxx.ddd.application.mapper;

import org.springframework.beans.BeanUtils;

import com.xxxx.ddd.application.model.TicketDTO;
import com.xxxx.ddd.domain.model.entity.Ticket;

public class TicketMapper {
    public static TicketDTO toDTO(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        TicketDTO dto = new TicketDTO();
        BeanUtils.copyProperties(ticket, dto);
        return dto;
    }
}
