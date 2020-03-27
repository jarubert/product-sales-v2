package com.jarubert.api.validators;

import com.jarubert.api.exceptions.ProductNotFoundException;
import com.jarubert.api.model.dto.OrderEntryPostDto;
import com.jarubert.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class OrderEntryValidator implements Validator {
    @Autowired
    private ProductService productService;

    @Override
    public boolean supports(Class<?> aClass) {
        return OrderEntryPostDto.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (errors.getErrorCount() == 0) {
            OrderEntryPostDto entry = (OrderEntryPostDto) o;
            if (productService.getOneById(entry.getProductId()) == null) {
                throw new ProductNotFoundException(entry.getProductId());
            }
        }
    }

}
