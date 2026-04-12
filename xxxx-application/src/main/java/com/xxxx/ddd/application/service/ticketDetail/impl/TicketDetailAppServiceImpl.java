package com.xxxx.ddd.application.service.ticketDetail.impl;

import org.springframework.stereotype.Service;

import com.xxxx.ddd.application.mapper.TicketDetailMapper;
import com.xxxx.ddd.application.model.TicketDetailDTO;
import com.xxxx.ddd.application.model.cache.TicketDetailCache;
import com.xxxx.ddd.application.service.ticketDetail.TicketDetailAppService;
import com.xxxx.ddd.application.service.ticketDetail.cache.TicketDetailCacheServiceRefactor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailAppServiceImpl implements TicketDetailAppService {

    private final TicketDetailCacheServiceRefactor ticketDetailCacheServiceRefactor;

    @Override
    public TicketDetailDTO getTicketDetailById(Long ticketId, Long version) {
        log.info("Implement Application : {}, {}: ", ticketId, version);
        TicketDetailCache ticketDetailCache = ticketDetailCacheServiceRefactor.getTicketDetail(ticketId, version);
        TicketDetailDTO ticketDetailDTO = TicketDetailMapper
                .mapperToTicketDetailDTO(ticketDetailCache.getTicketDetail());
        ticketDetailDTO.setVersion(ticketDetailCache.getVersion());
        return ticketDetailDTO;
    }

    @Override
    public boolean orderTicketByUser(Long ticketId, Long userId, int quantity) {
        return ticketDetailCacheServiceRefactor.orderTicketByUser(ticketId, userId, quantity);
    }
}
