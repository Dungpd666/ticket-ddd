package com.xxxx.ddd.controller.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xxxx.ddd.application.model.TicketDTO;
import com.xxxx.ddd.application.model.TicketDetailDTO;
import com.xxxx.ddd.application.service.ticket.TicketAppService;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.vo.PageResult;
import com.xxxx.ddd.controller.model.vo.ResultMessage;
import com.xxxx.ddd.domain.model.enums.TicketStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketAppService ticketAppService;

    @GetMapping("/{ticketId}")
    public ResultMessage<TicketDTO> getTicketById(@PathVariable Long ticketId) {
        return ResultUtil.data(ticketAppService.getTicketById(ticketId));
    }

    @GetMapping
    public ResultMessage<PageResult<TicketDTO>> listTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResultUtil.data(PageResult.of(ticketAppService.listTickets(status, page, size)));
    }

    @GetMapping("/{ticketId}/details")
    public ResultMessage<PageResult<TicketDetailDTO>> listTicketDetails(
            @PathVariable Long ticketId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResultUtil.data(PageResult.of(ticketAppService.listTicketDetails(ticketId, page, size)));
    }
}
