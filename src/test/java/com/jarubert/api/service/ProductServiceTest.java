package com.jarubert.api.service;

import com.jarubert.api.model.dto.ProductDto;
import com.jarubert.api.model.entity.Price;
import com.jarubert.api.model.entity.Product;
import com.jarubert.api.repository.PriceRepository;
import com.jarubert.api.repository.ProductRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ProductServiceTest {
    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Test
    public void shouldReturnInsertedProduct() {
        ProductDto returnedProduct = productService.getOneByName("Pipe");
        Assert.assertNull(returnedProduct);

        ProductDto productToAdd = new ProductDto("Pipe", "Wood pipe", 150.0);

        ProductDto addedProduct = productService.insertOrUpdateProduct(productToAdd);
        Assert.assertNotNull(addedProduct);

        List<ProductDto> returnedProducts = productService.getAll();
        Assert.assertEquals(1, returnedProducts.size());
        compareProduct(productToAdd, returnedProducts.get(0), false);
    }

    @Test
    public void shouldReturnAddedProductById() {
        Product addedProduct = productRepository.save(new Product("Pipe", "Wood pipe"));
        addedProduct.setPrices(Collections.singletonList(priceRepository.save(new Price(addedProduct, LocalDateTime.now(), null, 150.0))));
        Assert.assertNotNull(addedProduct);

        ProductDto returnedProduct = productService.getOneById(addedProduct.getId());
        Assert.assertNotNull(returnedProduct);
        compareProduct(addedProduct, returnedProduct);
    }

    @Test
    public void shouldReturnAddedProductByName() {
        ProductDto returnedProduct = productService.getOneByName("Pipe");
        Assert.assertNull(returnedProduct);

        Product addedProduct = productRepository.save(new Product("Pipe", "Wood pipe"));
        addedProduct.setPrices(Collections.singletonList(priceRepository.save(new Price(addedProduct, LocalDateTime.now(), null, 150.0))));
        Assert.assertNotNull(addedProduct);

        returnedProduct = productService.getOneByName(addedProduct.getName());
        Assert.assertNotNull(returnedProduct);
        compareProduct(addedProduct, returnedProduct);
    }

    @Test
    public void shouldReturnAllAddedProducts() {
        List<ProductDto> returnedProducts = productService.getAll();
        Assert.assertEquals(new ArrayList<>(), returnedProducts);

        List<Product> addedProducts = new ArrayList();
        addedProducts.add(productRepository.save(new Product("Pipe", "Wood pipe")));
        addedProducts.get(0).setPrices(Collections.singletonList(priceRepository.save(new Price(addedProducts.get(0), LocalDateTime.now(), null, 150.0))));
        addedProducts.add(productRepository.save(new Product("Grass", "Smokable grass")));
        addedProducts.get(1).setPrices(Collections.singletonList(priceRepository.save(new Price(addedProducts.get(1), LocalDateTime.now(), null, 17.0))));

        //reverse to match return order
        Collections.reverse(addedProducts);

        returnedProducts = productService.getAll();
        Assert.assertEquals(addedProducts.size(), returnedProducts.size());
        for (int i = 0; i < addedProducts.size(); i++) {
            compareProduct(addedProducts.get(i), returnedProducts.get(i));
        }
    }

    private void compareProduct(Product product, ProductDto productDto) {
        assertEquals(product.getId(), productDto.getId().get());
        assertEquals(product.getName(), productDto.getName().get());
        assertEquals(product.getDescription(), productDto.getDescription().get());
        assertEquals(product.getPrices().get(0).getValue(), productDto.getPrice().get());
    }

    private void compareProduct(ProductDto productDto, ProductDto productDtoResponse, boolean checkId) {
        if (checkId) {
            assertEquals(productDto.getId().get(), productDtoResponse.getId().get());
        }
        assertEquals(productDto.getName().get(), productDtoResponse.getName().get());
        assertEquals(productDto.getDescription().get(), productDtoResponse.getDescription().get());
        assertEquals(productDto.getPrice().get(), productDtoResponse.getPrice().get());
    }

}
