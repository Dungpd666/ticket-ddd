package com.xxxx.ddd.controller.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "userId is required")
    @Min(value = 1, message = "userId must be positive")
    private Long userId;
}
