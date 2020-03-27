package com.jarubert.api.controller.advice;

import com.jarubert.api.exceptions.OrderInvalidBuyerException;
import com.jarubert.api.model.dto.ApiErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class OrderInvalidBuyerAdvice {

    @ResponseBody
    @ExceptionHandler(OrderInvalidBuyerException.class)
    public ResponseEntity orderInvalidBuyerHandler(OrderInvalidBuyerException ex) {
        return new ResponseEntity(new ApiErrorDto(ex, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
}
