package com.jarubert.api.service;

import com.jarubert.api.model.entity.Price;
import com.jarubert.api.model.entity.Product;
import com.jarubert.api.model.dto.ProductDto;
import com.jarubert.api.repository.PriceRepository;
import com.jarubert.api.repository.ProductRepository;
import com.jarubert.api.util.PriceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by jarubert on 2020-03-24.
 */
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PriceRepository priceRepository;

    public ProductDto getOneById(Long id) {
        return productRepository.findById(id)
                .map(product -> new ProductDto(product))
                .orElse(null);
    }

    public ProductDto getOneByName(String name) {
        return productRepository.findOneByName(name)
                .map(product -> new ProductDto(product))
                .orElse(null);
    }

    public List<ProductDto> getAll() {
        return productRepository.findAllByOrderByIdDesc().stream().map(product -> new ProductDto(product)).collect(Collectors.toList());
    }

    public ProductDto insertOrUpdateProduct(ProductDto newProduct) {
        if (newProduct.getId().isPresent()) {
            return updateProduct(newProduct);
        }
        Product product = productRepository.save(new Product(newProduct));
        product.setPrices(Collections.singletonList(insertProductPrice(product, newProduct.getPrice().get())));
        return new ProductDto(product);
    }

    private Price insertProductPrice(Product product, Double currentPrice) {
        return priceRepository.save(new Price(product, LocalDateTime.now(), null, currentPrice));
    }

    private ProductDto updateProduct(ProductDto newProduct) {

        Product existingProduct = productRepository.findById(newProduct.getId().get()).get();
        if (!validateNeedsUpdate(newProduct, existingProduct)){
            return new ProductDto(existingProduct);
        }
        newProduct.getName().ifPresent(name -> existingProduct.setName(name));
        newProduct.getDescription().ifPresent(description -> existingProduct.setDescription(description));

        ProductDto response = new ProductDto(productRepository.save(existingProduct));

        if (newProduct.getPrice().isPresent()) {
            updatePrice(newProduct.getPrice().get(), existingProduct);
            response.setPrice(newProduct.getPrice());
        }

        return response;
    }

    private boolean validateNeedsUpdate(ProductDto newProduct, Product existingProduct) {
        if (newProduct.getName().isPresent() && !newProduct.getName().get().equals(existingProduct.getName())) {
            return true;
        }
        if (newProduct.getDescription().isPresent() && !newProduct.getDescription().get().equals(existingProduct.getDescription())) {
            return true;
        }
        if (newProduct.getPrice().isPresent() && !newProduct.getPrice().get().equals(PriceUtil.getCurrentPrice(existingProduct))) {
            return true;
        }
        return false;
    }

    private void updatePrice(Double currentPrice, Product existingProduct) {
        List<Price> prices = existingProduct.getPrices();
        Price oldPrice = prices.stream().filter(p -> p.getValidTo() == null).findFirst().get();
        oldPrice.setValidTo(LocalDateTime.now());
        priceRepository.save(oldPrice);

        insertProductPrice(existingProduct, currentPrice);
    }
}
