package com.xxxx.ddd.controller.model.vo;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private List<T> items;
    private long total;
    private int page;
    private int size;

    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
    }
}
