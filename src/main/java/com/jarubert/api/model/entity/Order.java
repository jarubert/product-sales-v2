package com.jarubert.api.model.entity;

import com.jarubert.api.model.dto.OrderDto;
import com.jarubert.api.model.dto.OrderPostDto;
import com.jarubert.api.model.dto.Status;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarubert on 2020-03-24.
 */
@Data
@Entity
@Table(name = "order_table")
public class Order {
    private @Id @GeneratedValue Long id;
    private LocalDateTime date;
    private String buyer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OrderEntry> orderEntries = new ArrayList<OrderEntry>();
    @Enumerated(EnumType.STRING)
    private Status status;

    public Order() {
    }

    public Order(LocalDateTime date, String buyer, Status status) {
        this.date = date;
        this.buyer = buyer;
        this.status = status;
    }

    public Order(OrderDto newOrder) {
        this.date = LocalDateTime.now();
        this.buyer = newOrder.getBuyer();
        this.status = newOrder.getStatus();
    }

    public Order(OrderPostDto newOrder) {
        this.date = LocalDateTime.now();
        this.buyer = newOrder.getBuyer();
        this.status = newOrder.getStatus();
    }

}
