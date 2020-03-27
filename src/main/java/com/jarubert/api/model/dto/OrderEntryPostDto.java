package com.jarubert.api.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by jarubert on 2020-03-25.
 */
@Data
public class OrderEntryPostDto {
    @NotNull(message = "Field 'productId' of 'entries' is mandatory")
    private Long productId;
    @NotNull(message = "Field 'quantity' of 'entries' is mandatory")
    @Min(value = 0, message = "Field 'quantity' of 'entries' must be greater or equal to zero")
    private Integer quantity;

    public OrderEntryPostDto() {
    }

    public OrderEntryPostDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
