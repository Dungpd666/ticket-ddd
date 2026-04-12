package com.xxxx.ddd.domain.model.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import com.xxxx.ddd.domain.model.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ticket {

    @Id
    private Long id;

    private String name;
    @Column(name = "desc")
    private String description;
    private Date startTime;
    private Date endTime;

    @Enumerated(EnumType.ORDINAL)
    private TicketStatus status;
    private Date updatedAt;
    private Date createdAt;

}
