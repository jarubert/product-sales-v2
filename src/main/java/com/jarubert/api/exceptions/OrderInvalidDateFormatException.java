package com.jarubert.api.exceptions;

public class OrderInvalidDateFormatException extends RuntimeException  {
    public OrderInvalidDateFormatException(String field, String date) {
        super("The provided " + field + " '" + date + "' is not in a valid format, the correct format is dd-MM-yyyy HH:mm:ss, ex: 31-12-2020 15:30:59");
    }
}
