package com.jarubert.api.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Price {
    private @Id @GeneratedValue Long id;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "product_id")
    private Product product;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Double value;

    public Price() {
    }

    public Price(Product product, LocalDateTime validFrom, LocalDateTime validTo, Double value) {
        this.product = product;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.value = value;
    }
}
