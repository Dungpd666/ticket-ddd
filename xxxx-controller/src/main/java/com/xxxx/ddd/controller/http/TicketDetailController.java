package com.xxxx.ddd.controller.http;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xxxx.ddd.application.model.TicketDetailDTO;
import com.xxxx.ddd.application.service.ticket.TicketDetailAppService;
import com.xxxx.ddd.controller.model.enums.ResultCode;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.request.OrderRequest;
import com.xxxx.ddd.controller.model.vo.ResultMessage;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/ticket")
@Slf4j
public class TicketDetailController {

    // CALL Service Application
    @Autowired
    private TicketDetailAppService ticketDetailAppService;

    @GetMapping("/ping/java")
    public ResponseEntity<Object> ping() throws InterruptedException {
        // Giả lập tác vụ mất thời gian
        Thread.sleep(1000); // Giống như time.Sleep(1 * time.Second)

        // Trả về response với status OK
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response("OK"));
    }

    // Lớp Response để trả về JSON response
    public static class Response {
        private String status;

        public Response(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    /**
     * Get ticket detail
     * 
     * @param ticketId
     * @param detailId
     * @return ResultUtil
     */
    @GetMapping("/{ticketId}/detail/{detailId}")
    public ResultMessage<TicketDetailDTO> getTicketDetail(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("detailId") Long detailId,
            @RequestParam(name = "version", required = false) Long version) {
        return ResultUtil.data(ticketDetailAppService.getTicketDetailById(detailId, version));
    }

    /**
     * order by User
     * 
     * @param ticketId
     * @param detailId
     * @return ResultUtil
     */
    @PostMapping("/{ticketId}/detail/{detailId}/order")
    public ResultMessage<Boolean> orderTicketByUser(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("detailId") Long detailId,
            @RequestBody @Valid OrderRequest request) {
        boolean result = ticketDetailAppService.orderTicketByUser(detailId, request.getUserId());
        return result ? ResultUtil.data(true) : ResultUtil.error(ResultCode.UN_ERROR.code(), "Order failed");
    }

}
