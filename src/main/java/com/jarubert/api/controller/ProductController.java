package com.jarubert.api.controller;

import com.jarubert.api.model.dto.ProductDto;
import com.jarubert.api.service.ProductService;
import com.jarubert.api.validators.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by jarubert on 2020-03-24.
 */
@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductValidator productValidator;

    @InitBinder("productDto")
    public void initMerchantOnlyBinder(WebDataBinder binder) {
        binder.addValidators(productValidator);
    }

    @GetMapping(value = "/{id}")
    public ProductDto getOne(@PathVariable Long id) {
        return productService.getOneById(id);
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return productService.getAll();
    }

    @PostMapping
    public ResponseEntity<ProductDto> postProduct(@Valid @RequestBody ProductDto newProduct, Errors errors) {
        return new ResponseEntity(productService.insertOrUpdateProduct(newProduct), newProduct.getId().isPresent() ?
                HttpStatus.OK : HttpStatus.CREATED);
    }
}
