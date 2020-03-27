package com.jarubert.api.util;

import com.jarubert.api.model.entity.Price;
import com.jarubert.api.model.entity.Product;

import java.time.LocalDateTime;

public class PriceUtil {

    public static Double getOrderedPrice(Product product, LocalDateTime orderDate) {
        Price validPrice = product.getPrices().stream().
                filter(p -> p.getValidFrom().isBefore(orderDate) &&
                        ((p.getValidTo() != null && p.getValidTo().isAfter(orderDate)) ||
                                p.getValidTo() == null)
                ).findFirst().get();
        return validPrice.getValue();
    }

    public static Double getCurrentPrice(Product product) {
        return product.getPrices().stream().filter(p -> p.getValidTo() == null).findFirst().get().getValue();
    }
}
