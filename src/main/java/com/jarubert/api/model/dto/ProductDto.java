package com.jarubert.api.model.dto;

import com.jarubert.api.model.entity.Price;
import com.jarubert.api.model.entity.Product;
import com.jarubert.api.util.PriceUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by jarubert on 2020-03-24.
 */
@Data
public class ProductDto {
    private Optional<Long> id = Optional.empty();
    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<Double> price = Optional.empty();

    public ProductDto() {
    }

    public ProductDto(String name, String description, Double price) {
        this.name = Optional.ofNullable(name);
        this.description = Optional.ofNullable(description);
        this.price = Optional.ofNullable(price);
    }

    public ProductDto(Long id, String name, String description, Double price) {
        this.id = Optional.ofNullable(id);
        this.name = Optional.ofNullable(name);
        this.description = Optional.ofNullable(description);
        this.price = Optional.ofNullable(price);
    }

    public ProductDto(Product product) {
        this.id = Optional.ofNullable(product.getId());
        this.name = Optional.ofNullable(product.getName());
        this.description = Optional.ofNullable(product.getDescription());
        this.price = Optional.ofNullable(PriceUtil.getCurrentPrice(product));
    }

    public ProductDto(Product product, LocalDateTime orderDate) {
        this.id = Optional.ofNullable(product.getId());
        this.name = Optional.ofNullable(product.getName());
        this.description = Optional.ofNullable(product.getDescription());
        this.price = Optional.ofNullable(PriceUtil.getOrderedPrice(product, orderDate));
    }

}
