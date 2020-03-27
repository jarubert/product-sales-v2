package com.jarubert.api.model.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ApiErrorDto {
    private String timestamp;
    private int status;
    private String error;
    private List<String> messages;

    public ApiErrorDto() {
    }

    public ApiErrorDto(Errors errors, HttpStatus httpStatus) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.messages = errors.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
    }

    public ApiErrorDto(Exception e, HttpStatus httpStatus) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.messages = Collections.singletonList(e.getMessage());
    }


}
