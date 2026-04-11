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

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultMessage<Void> handleRuntimeException(RuntimeException ex) {
        return ResultUtil.error(ResultCode.ERROR.code(), ex.getMessage());
    }
}
