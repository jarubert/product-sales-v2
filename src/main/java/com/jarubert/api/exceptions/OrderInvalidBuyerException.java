package com.jarubert.api.exceptions;

public class OrderInvalidBuyerException extends RuntimeException  {
    public OrderInvalidBuyerException(String buyer) {
        super("The given buyer '" + buyer + "' is not a valid e-mail");
    }
}
