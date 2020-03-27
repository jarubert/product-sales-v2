package com.jarubert.api.exceptions;

public class ProductNameConflictException extends RuntimeException  {
    public ProductNameConflictException(String name) {
        super("There is already a product with the given name " + name);
    }
}
