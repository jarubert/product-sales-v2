package com.jarubert.api.exceptions;

public class ProductInvalidPriceException extends RuntimeException  {
    public ProductInvalidPriceException(Double price) {
        super("The given price '" + price + "' is not a valid positive number");
    }
}
