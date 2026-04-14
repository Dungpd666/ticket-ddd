package com.xxxx.ddd.controller.advice;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.xxxx.ddd.controller.model.enums.ResultCode;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.vo.ResultMessage;
import com.xxxx.ddd.domain.exception.OrderNotAllowedException;
import com.xxxx.ddd.domain.exception.OrderNotFoundException;
import com.xxxx.ddd.domain.exception.UnauthorizedException;
import com.xxxx.ddd.domain.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultMessage<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ResultUtil.error(ResultCode.PARAMS_ERROR.code(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultMessage<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResultUtil.error(ResultCode.PARAMS_ERROR.code(), message);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultMessage<Void> handleUnauthorized(UnauthorizedException ex) {
        return ResultUtil.error(ResultCode.USER_UNAUTHORIZED.code(), ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultMessage<Void> handleOrderNotFound(OrderNotFoundException ex) {
        return ResultUtil.error(ResultCode.ERROR.code(), ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultMessage<Void> handleUserNotFound(UserNotFoundException ex) {
        return ResultUtil.error(ResultCode.USER_NOT_FOUND.code(), ex.getMessage());
    }

    @ExceptionHandler(OrderNotAllowedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResultMessage<Void> handleOrderNotAllowed(OrderNotAllowedException ex) {
        return ResultUtil.error(ResultCode.PARAMS_ERROR.code(), ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultMessage<Void> handleRuntimeException(RuntimeException ex) {
        return ResultUtil.error(ResultCode.UN_ERROR.code(), ex.getMessage());
    }
}
