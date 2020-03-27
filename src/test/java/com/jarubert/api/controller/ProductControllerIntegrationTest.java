package com.jarubert.api.controller;

import com.jarubert.api.model.dto.ApiErrorDto;
import com.jarubert.api.model.dto.ProductDto;
import com.jarubert.api.util.RollbackUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by jarubert on 2020-03-26.
 */

public class ProductControllerIntegrationTest extends AbstractControllerIntegrationTest {

    /*
    https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/html/boot-features-testing.html

    If your test is @Transactional, it rolls back the transaction at the end of each test method by default. However, as
     using this arrangement with either RANDOM_PORT or DEFINED_PORT implicitly provides a real servlet environment, the
     HTTP client and server run in separate threads and, thus, in separate transactions. Any transaction initiated on
     the server does not roll back in this case.
     */
    @Autowired
    private RollbackUtil rollbackUtil;

    @Before
    public void setUp() {
        super.setUp();
        baseUrl = baseUrl + "products";
    }

    @After
    public void cleanUp() {
        rollbackUtil.rollbackProducts();
    }

    @Test
    public void onUpdateShouldNotAllowNegativePrice() {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                baseUrl, new ProductDto("updateNullPrice", "Wood pipe", 10.0), ProductDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProductDto updateProduct = response.getBody();
        updateProduct.setPrice(Optional.of(-1.0));

        ResponseEntity<ApiErrorDto> responseError = restTemplate.postForEntity(
                baseUrl, updateProduct, ApiErrorDto.class);
        assertErrorHttpStatus(responseError, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("The given price '-1.0' is not a valid positive number"), responseError.getBody().getMessages());
    }

    @Test
    public void onUpdateShouldNotAllowEmptyName() {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                baseUrl, new ProductDto("updateEmptyName", "Wood pipe", 10.0), ProductDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProductDto updateProduct = response.getBody();
        updateProduct.setName(Optional.of(""));

        ResponseEntity<ApiErrorDto> responseError = restTemplate.postForEntity(
                baseUrl, updateProduct, ApiErrorDto.class);
        assertNullField(responseError, "name");
    }

    @Test
    public void updateProductProperties() {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                baseUrl, new ProductDto("updateProduct", "Wood pipe", 10.0), ProductDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ProductDto updateProduct = response.getBody();
        updateProduct.setDescription(Optional.of("updated Description"));
        updateProduct.setPrice(Optional.of(123.0));

        response = restTemplate.postForEntity(baseUrl, updateProduct, ProductDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<ProductDto> updatedResponse =
                restTemplate.getForEntity(baseUrl + "/" + updateProduct.getId().get(), ProductDto.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());

        compareProduct(updateProduct, updatedResponse.getBody());
    }

    @Test
    public void onUpdateShouldNotAllowSameName() {
        //Create with name 'updateSameName'
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                baseUrl, new ProductDto("updateSameName", "Wood pipe", 10.0), ProductDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        //Create with name 'updateDifferentName'
        response = restTemplate.postForEntity(
                baseUrl, new ProductDto("updateDifferentName", "Wood pipe", 10.0), ProductDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        //try to update to a name that already exists
        ResponseEntity<ApiErrorDto> responseError = restTemplate.postForEntity(
                baseUrl, new ProductDto(response.getBody().getId().get(),"updateSameName", null, null), ApiErrorDto.class);
        assertErrorHttpStatus(responseError, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("There is already a product with the given name updateSameName"), responseError.getBody().getMessages());

        //check that you can update the other properties of the product containing its own name
        response = restTemplate.postForEntity(
                baseUrl, new ProductDto(response.getBody().getId().get(),"updateDifferentName", "Iron pipe", 15.0), ProductDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Iron pipe", response.getBody().getDescription().get());
        assertEquals(Double.valueOf(15.0), response.getBody().getPrice().get());
    }

    @Test
    public void onInsertShouldNotAllowSameName() {
        //Create with name 'sameName'
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                baseUrl, new ProductDto("sameName", "Wood pipe", 10.0), ProductDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        //Create with another product with name 'sameName'
        ResponseEntity<ApiErrorDto> responseError = restTemplate.postForEntity(
                baseUrl, new ProductDto("sameName", "pipe", 12.0), ApiErrorDto.class);
        assertErrorHttpStatus(responseError, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("There is already a product with the given name sameName"), responseError.getBody().getMessages());
    }

    @Test
    public void onInsertShouldNotAllowNegativePrice() {
        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                baseUrl, new ProductDto("negativePrice", "Wood pipe", -10.0), ApiErrorDto.class);
        assertErrorHttpStatus(response, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("The given price '-10.0' is not a valid positive number"), response.getBody().getMessages());
    }

    @Test
    public void onInsertShouldNotAllowNullPrice() {
        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                baseUrl, new ProductDto("nullPrice", "Wood pipe", null), ApiErrorDto.class);
        assertNullField(response, "price");
    }

    @Test
    public void onInsertShouldNotAllowNullName() {
        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                baseUrl, new ProductDto(null, "Wood pipe", 10.0), ApiErrorDto.class);
        assertNullField(response, "name");
    }

    @Test
    public void shouldReturnAddedProducts() throws Exception {
        List<ProductDto> products = initProducts();

        ResponseEntity<ProductDto[]> response = restTemplate.getForEntity(
                baseUrl, ProductDto[].class);
        final List<ProductDto> responseProducts = Arrays.asList(response.getBody());

        Assert.assertEquals(3, response.getBody().length);
        for (int i = 0; i < responseProducts.size(); i++){
            compareProduct(products.get(i), responseProducts.get(i));
        }
    }

    private List<ProductDto> initProducts() {
        List<ProductDto> products = new ArrayList<>();
        products.add(addProduct(new ProductDto("Pipe", "Wood pipe", 150.0)).getBody());
        products.add(addProduct(new ProductDto( "Bag", "Cloth Bag", 50.0)).getBody());
        products.add(addProduct(new ProductDto( "Purse", "Pink Purse", 500.0)).getBody());

        //need to reverse to keep the same order that is returned by the controller, ID Desc
        Collections.reverse(products);

        return products;
    }

    private ResponseEntity<ProductDto> addProduct(ProductDto product) {
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                baseUrl, product, ProductDto.class);

        return response;
    }

    private void compareProduct(ProductDto productDto, ProductDto productDtoResponse) {
        assertEquals(productDto.getId().get(), productDtoResponse.getId().get());
        assertEquals(productDto.getName().get(), productDtoResponse.getName().get());
        assertEquals(productDto.getDescription().get(), productDtoResponse.getDescription().get());
        assertEquals(productDto.getPrice().get(), productDtoResponse.getPrice().get());
    }


}
