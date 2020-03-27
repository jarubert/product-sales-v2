package com.jarubert.api.controller;

import com.jarubert.api.exceptions.OrderInvalidDateFormatException;
import com.jarubert.api.exceptions.OrderInvalidDatePeriodException;
import com.jarubert.api.model.dto.ApiErrorDto;
import com.jarubert.api.model.dto.OrderDto;
import com.jarubert.api.model.dto.OrderPostDto;
import com.jarubert.api.service.OrderService;
import com.jarubert.api.validators.OrderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Created by jarubert on 2020-03-24.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderValidator orderValidator;

    @InitBinder("orderPostDto")
    public void initMerchantOnlyBinder(WebDataBinder binder) {
        binder.addValidators(orderValidator);
    }

    @GetMapping
    public List<OrderDto> getAllInBetween(@RequestParam(name = "startDate") String startDateParameter,
                                   @RequestParam(name = "endDate") String endDateParameter) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            startDate = LocalDateTime.parse(startDateParameter, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        } catch (DateTimeParseException ex) {
            throw new OrderInvalidDateFormatException("startDate", startDateParameter);
        }
        try {
            endDate = LocalDateTime.parse(endDateParameter, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        } catch (DateTimeParseException ex) {
            throw new OrderInvalidDateFormatException("endDate", endDateParameter);
        }

        if (startDate.isAfter(endDate)) {
            throw new OrderInvalidDatePeriodException(startDateParameter, endDateParameter);
        }

        return orderService.getAllInBetween(startDate, endDate);
    }

    @PostMapping
    public ResponseEntity postOrder(@Valid @RequestBody OrderPostDto orderPostDto, Errors errors) {
        if (errors.hasErrors()) {
            return new ResponseEntity(new ApiErrorDto(errors, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(orderService.placeOrder(orderPostDto), HttpStatus.CREATED);
    }
}
