package com.jarubert.api.validators;

import com.jarubert.api.exceptions.OrderInvalidBuyerException;
import com.jarubert.api.model.dto.OrderPostDto;
import com.jarubert.api.model.dto.ProductDto;
import com.jarubert.api.model.entity.OrderEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class OrderValidator implements Validator {

    @Autowired
    private OrderEntryValidator orderEntryValidator;

    @Override
    public boolean supports(Class<?> aClass) {
        return OrderPostDto.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (errors.getErrorCount() == 0) {
            OrderPostDto order = (OrderPostDto) o;
            if (!isValidEmail(order.getBuyer())) {
                throw new OrderInvalidBuyerException(order.getBuyer());
            }

            order.getEntries().forEach(e -> orderEntryValidator.validate(e, errors));
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
