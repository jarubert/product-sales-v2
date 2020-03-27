package com.jarubert.api.model.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by jarubert on 2020-03-24.
 */
@Data
@Entity
public class OrderEntry {
    private @Id @GeneratedValue Long id;
    private Integer sequence;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer quantity;

    public OrderEntry() {
    }

    public OrderEntry(Order order, Integer sequence, Product product, Integer quantity) {
        this.order = order;
        this.sequence = sequence;
        this.product = product;
        this.quantity = quantity;
    }
}
