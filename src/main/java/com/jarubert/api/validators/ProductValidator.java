package com.jarubert.api.validators;

import com.jarubert.api.exceptions.ProductFieldNullException;
import com.jarubert.api.exceptions.ProductInvalidPriceException;
import com.jarubert.api.exceptions.ProductNameConflictException;
import com.jarubert.api.exceptions.ProductNotFoundException;
import com.jarubert.api.model.dto.ProductDto;
import com.jarubert.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProductValidator implements Validator {
    @Autowired
    private ProductService productService;

    @Override
    public boolean supports(Class<?> aClass) {
        return ProductDto.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (errors.getErrorCount() == 0) {
            ProductDto product = (ProductDto) o;

            if (product.getId().isPresent()) {
                validateUpdate(product);
            } else {
                validateInsert(product);
            }
        }
    }

    private void validateInsert(ProductDto product) {
        if (product.getName().isPresent() && !validateNameExists(product.getName().get(), null)) {
                throw new ProductNameConflictException(product.getName().get());
        }
        if (!product.getName().isPresent() || (product.getName().isPresent() && StringUtils.isEmpty(product.getName()))) {
            throw new ProductFieldNullException("name");
        }
        if (!product.getPrice().isPresent() || (product.getPrice().isPresent() && product.getPrice().get() == null)) {
            throw new ProductFieldNullException("price");
        }
        if (!isValidPrice(product.getPrice().get())) {
            throw new ProductInvalidPriceException(product.getPrice().get());
        }
    }

    private void validateUpdate(ProductDto product) {
        if (productService.getOneById(product.getId().get()) == null) {
            throw new ProductNotFoundException(product.getId().get());
        }
        if (product.getName().isPresent()) {
            if (!validateNameExists(product.getName().get(), product.getId().get())) {
                throw new ProductNameConflictException(product.getName().get());
            }
            if (StringUtils.isEmpty(product.getName().get())) {
                throw new ProductFieldNullException("name");
            }
        }
        if (product.getPrice().isPresent()) {
            if (product.getPrice().get() == null) {
                throw new ProductFieldNullException("price");
            } else if (!isValidPrice(product.getPrice().get())) {
                throw new ProductInvalidPriceException(product.getPrice().get());
            }
        }
    }

    private boolean validateNameExists(String name, Long productId) {
        ProductDto existingProduct = productService.getOneByName(name);
        if (productId != null && existingProduct != null) {
            return existingProduct.getId().get() == productId;
        }
        return existingProduct == null;
    }

    private boolean isValidPrice(Double price) {
        if (price < 0) {
            return false;
        }
        return true;
    }
}
