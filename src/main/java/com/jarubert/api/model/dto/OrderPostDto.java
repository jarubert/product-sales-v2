package com.jarubert.api.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by jarubert on 2020-03-25.
 */
@Data
public class OrderPostDto {
    @NotBlank(message = "Field 'buyer' is mandatory")
    private String buyer;
    @NotEmpty(message = "Field 'entries' is mandatory")
    @Valid
    private List<OrderEntryPostDto> entries;
    @NotNull(message = "Field 'status' is mandatory, valid values are PLACED, APPROVED OR DELIVERED")
    private Status status;

    public OrderPostDto() {
    }

    public OrderPostDto(String buyer, List<OrderEntryPostDto> entries, Status status) {
        this.buyer = buyer;
        this.entries = entries;
        this.status = status;
    }
}
