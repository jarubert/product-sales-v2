package com.jarubert.api.controller.advice;

import com.jarubert.api.exceptions.ProductNotFoundException;
import com.jarubert.api.model.dto.ApiErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ProductNotFoundAdvice{
    @ResponseBody
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity productNotFoundHandler(ProductNotFoundException ex) {
        return new ResponseEntity(new ApiErrorDto(ex, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }
}
