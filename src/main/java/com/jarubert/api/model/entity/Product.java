package com.jarubert.api.model.entity;

import com.jarubert.api.model.dto.ProductDto;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by jarubert on 2020-03-24.
 */
@Data
@Entity
public class Product {
    private @Id @GeneratedValue Long id;
    private String name;
    private String description;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Price> prices;

    public Product() {
    }

    public Product(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Product(ProductDto product) {
        product.getId().ifPresent(id -> this.id = id);
        product.getName().ifPresent(name -> this.name = name);
        product.getDescription().ifPresent(description -> this.description = description);
    }
}
