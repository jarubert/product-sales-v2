package com.jarubert.api.model.dto;

import com.jarubert.api.model.entity.Order;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarubert on 2020-03-24.
 */
@Data
public class OrderDto {
    private Long id;
    private LocalDateTime date;
    private String buyer;
    private List<OrderEntryDto> entries = new ArrayList<OrderEntryDto>();
    private Double totalPrice;
    private Status status;

    public OrderDto() {
    }

    public OrderDto(Order order) {
        this.id = order.getId();
        this.buyer = order.getBuyer();
        this.date = order.getDate();
        this.status = order.getStatus();
        this.entries.addAll(order.getOrderEntries().stream().
                map(orderEntry -> new OrderEntryDto(orderEntry, order.getDate())).collect(Collectors.toList()));
        calculateTotalPrice();
    }

    public void calculateTotalPrice() {
        this.totalPrice = this.entries.stream().mapToDouble(e -> e.getTotalPrice()).sum();
    }
}
