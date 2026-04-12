package com.xxxx.ddd.application.model;

import java.util.Date;

import com.xxxx.ddd.domain.model.enums.TicketStatus;

import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private String name;
    private String description;
    private Date startTime;
    private Date endTime;
    private TicketStatus status;
    private Date createdAt;
}
