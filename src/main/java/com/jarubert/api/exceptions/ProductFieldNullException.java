package com.jarubert.api.exceptions;

public class ProductFieldNullException extends RuntimeException  {
    public ProductFieldNullException(String field) {
        super("The field '" + field + "' can not be null");
    }
}
