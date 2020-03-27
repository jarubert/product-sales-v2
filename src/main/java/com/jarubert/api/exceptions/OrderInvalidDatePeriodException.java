package com.jarubert.api.exceptions;

public class OrderInvalidDatePeriodException extends RuntimeException  {
    public OrderInvalidDatePeriodException(String startDate, String endDate) {
        super("Invalid date period, the startDate must be before the endDate. Provided start: '" + startDate + "' endDate '" + endDate + "'.");
    }
}
