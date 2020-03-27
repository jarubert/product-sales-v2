package com.jarubert.api.model.dto;

import com.jarubert.api.model.entity.OrderEntry;
import com.jarubert.api.model.entity.Price;
import com.jarubert.api.model.entity.Product;
import com.jarubert.api.util.PriceUtil;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by jarubert on 2020-03-24.
 */
@Data
public class OrderEntryDto {
    private Integer sequence;
    private ProductDto product;
    private Integer quantity;
    private Double basePrice;
    private Double totalPrice;

    public OrderEntryDto() {
    }

    public OrderEntryDto(OrderEntry orderEntry, LocalDateTime orderDate) {
        this.sequence = orderEntry.getSequence();
        this.product = new ProductDto(orderEntry.getProduct(), orderDate);
        this.quantity = orderEntry.getQuantity();
        this.basePrice = product.getPrice().get();
        this.totalPrice = this.quantity * this.basePrice;
    }

}
