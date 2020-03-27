package com.jarubert.api.controller;

import com.jarubert.api.model.dto.*;
import com.jarubert.api.model.entity.Order;
import com.jarubert.api.repository.OrderRepository;
import com.jarubert.api.util.RollbackUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class OrderControllerIntegrationTest extends AbstractControllerIntegrationTest {

    /*
   https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/html/boot-features-testing.html

   If your test is @Transactional, it rolls back the transaction at the end of each test method by default. However, as
    using this arrangement with either RANDOM_PORT or DEFINED_PORT implicitly provides a real servlet environment, the
    HTTP client and server run in separate threads and, thus, in separate transactions. Any transaction initiated on
    the server does not roll back in this case.
    */
    @Autowired
    private RollbackUtil rollbackUtil;

    private String baseUrlProduct;

    @Autowired
    private OrderRepository orderRepository;

    @Before
    public void setUp() {
        super.setUp();
        baseUrlProduct = baseUrl + "products";
        baseUrl = baseUrl + "orders";
    }

    @After
    public void cleanUp() {
        rollbackUtil.rollbackOrders();
        rollbackUtil.rollbackProducts();
    }


    @Test
    public void shouldReturnBadRequestInvalidBuyer() {
        ProductDto product = addProduct(new ProductDto("Pipe", "Wood pipe", 150.0)).getBody();
        List<OrderEntryPostDto> entries = Collections.singletonList(new OrderEntryPostDto(product.getId().get(), 10));;
        OrderPostDto order = new OrderPostDto("testest.com", entries, Status.APPROVED);
        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                baseUrl, order, ApiErrorDto.class);
        assertErrorHttpStatus(response, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("The given buyer 'testest.com' is not a valid e-mail"), response.getBody().getMessages());
    }

    @Test
    public void shouldReturnBadRequestInvalidQuantity() {
        ProductDto product = addProduct(new ProductDto("Pipe", "Wood pipe", 150.0)).getBody();
        List<OrderEntryPostDto> entries = Collections.singletonList(new OrderEntryPostDto(product.getId().get(), -10));;
        OrderPostDto order = new OrderPostDto("test@test.com", entries, Status.APPROVED);
        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                baseUrl, order, ApiErrorDto.class);
        assertErrorHttpStatus(response, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("Field 'quantity' of 'entries' must be greater or equal to zero"), response.getBody().getMessages());
    }

    @Test
    public void shouldReturnNotFoundInvalidProductId() {
        List<OrderEntryPostDto> entries = Collections.singletonList(new OrderEntryPostDto(10L, 10));;
        OrderPostDto order = new OrderPostDto("test@test.com", entries, Status.APPROVED);
        ResponseEntity<ApiErrorDto> response = restTemplate.postForEntity(
                baseUrl, order, ApiErrorDto.class);
        assertErrorHttpStatus(response, HttpStatus.NOT_FOUND);
        Assert.assertEquals(Collections.singletonList("Could not find product with id 10"), response.getBody().getMessages());
    }

    @Test
    public void shouldReturnBadRequestInvalidDatePeriodParameter() {
        ResponseEntity<ApiErrorDto> response = restTemplate.getForEntity(
                baseUrl+"?startDate=22-10-2024 00:00:00&endDate=22-10-2019 00:00:00", ApiErrorDto.class);
        assertErrorHttpStatus(response, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("Invalid date period, the startDate must be before the endDate. " +
                "Provided start: '22-10-2024 00:00:00' endDate '22-10-2019 00:00:00'."), response.getBody().getMessages());
    }

    @Test
    public void shouldReturnBadRequestInvalidDateFormatParameter() {
        ResponseEntity<ApiErrorDto> response = restTemplate.getForEntity(
                baseUrl+"?startDate=22-10-20190:00:00&endDate=22-10-2023 00:00:00", ApiErrorDto.class);
        assertErrorHttpStatus(response, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("The provided startDate '22-10-20190:00:00' is not in a valid " +
                "format, the correct format is dd-MM-yyyy HH:mm:ss, ex: 31-12-2020 15:30:59"), response.getBody().getMessages());

        response = restTemplate.getForEntity(
                baseUrl+"?startDate=22-10-2019 00:00:00&endDate=22-10-20 00:00:00", ApiErrorDto.class);
        assertErrorHttpStatus(response, HttpStatus.BAD_REQUEST);
        Assert.assertEquals(Collections.singletonList("The provided endDate '22-10-20 00:00:00' is not in a valid " +
                "format, the correct format is dd-MM-yyyy HH:mm:ss, ex: 31-12-2020 15:30:59"), response.getBody().getMessages());
    }

    @Test
    public void shouldReturnPlacedOrders() {
        List<OrderDto> orders = initOrders();

        ResponseEntity<OrderDto[]> response = restTemplate.getForEntity(
                baseUrl+"?startDate=22-10-2019 00:00:00&endDate=22-10-2023 00:00:00", OrderDto[].class);
        final List<OrderDto> responseOrders = Arrays.asList(response.getBody());

        Assert.assertEquals(3, response.getBody().length);
        for(int i = 0; i < orders.size(); i++) {
            compareOrder(orders.get(i), responseOrders.get(i));
        }
    }

    @Test
    public void shouldKeepTotalPriceAfterProductPriceChange() {
        Double expectedPrice = 1500.0;

        ProductDto product = addProduct(new ProductDto("Pipe", "Wood pipe", 150.0)).getBody();
        List<OrderEntryPostDto> entries = Collections.singletonList(new OrderEntryPostDto(product.getId().get(), 10));;
        OrderPostDto order = new OrderPostDto("test@test.com", entries, Status.APPROVED);

        ResponseEntity<OrderDto> orderResponse = addOrder(order);
        Assert.assertEquals(HttpStatus.CREATED, orderResponse.getStatusCode());
        Assert.assertEquals(expectedPrice, orderResponse.getBody().getTotalPrice());

        ResponseEntity<OrderDto[]> response = restTemplate.getForEntity(
                baseUrl+"?startDate=22-10-2019 00:00:00&endDate=22-10-2023 00:00:00", OrderDto[].class);
        List<OrderDto> responseOrders = Arrays.asList(response.getBody());

        Assert.assertEquals(1, response.getBody().length);
        Assert.assertEquals(expectedPrice, responseOrders.get(0).getTotalPrice());

        //update Product Price
        product.setPrice(Optional.of(100000.0));
        ResponseEntity<ProductDto> responseProduct = restTemplate.postForEntity(baseUrlProduct, product, ProductDto.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(Double.valueOf(100000.0), responseProduct.getBody().getPrice().get());

        response = restTemplate.getForEntity(
                baseUrl+"?startDate=22-10-2019 00:00:00&endDate=22-10-2023 00:00:00", OrderDto[].class);
        responseOrders = Arrays.asList(response.getBody());

        Assert.assertEquals(1, response.getBody().length);
        //price remains the same
        Assert.assertEquals(expectedPrice, responseOrders.get(0).getTotalPrice());
    }

    @Test
    public void shouldReturnPlacedOrdersWithinDate() {
        //Create order 3 years from now
        Order order = orderRepository.save(new Order(LocalDateTime.now().plus(3, ChronoUnit.YEARS),
                "teste@teste.com", Status.APPROVED));
        String startDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now());
        String endDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now().plus(2, ChronoUnit.YEARS));
        //date period doesnt get the added order
        ResponseEntity<OrderDto[]> response = restTemplate.getForEntity(
                baseUrl+"?startDate="+startDate+"&endDate="+endDate, OrderDto[].class);
        List<OrderDto> responseOrders = Arrays.asList(response.getBody());

        Assert.assertEquals(0, response.getBody().length);

        endDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now().plus(4, ChronoUnit.YEARS));
        //date period gets the added order
        response = restTemplate.getForEntity(
                baseUrl+"?startDate="+startDate+"&endDate="+endDate, OrderDto[].class);
        responseOrders = Arrays.asList(response.getBody());

        Assert.assertEquals(1, response.getBody().length);
    }

    private void compareOrder(OrderDto expectedOrder, OrderDto responseOrder) {
        Assert.assertEquals(expectedOrder.getId(), responseOrder.getId());
        Assert.assertEquals(expectedOrder.getBuyer(), responseOrder.getBuyer());
        Assert.assertEquals(expectedOrder.getStatus(), responseOrder.getStatus());
        Assert.assertEquals(expectedOrder.getDate(), responseOrder.getDate());
        Assert.assertEquals(expectedOrder.getEntries().size(), responseOrder.getEntries().size());
        for (int i = 0; i < expectedOrder.getEntries().size(); i++) {
            compareEntry(expectedOrder.getEntries().get(i), responseOrder.getEntries().get(i));
        }
    }

    private void compareEntry(OrderEntryDto expectedEntry, OrderEntryDto responseEntry) {
        Assert.assertEquals(expectedEntry.getTotalPrice(), responseEntry.getTotalPrice());
        Assert.assertEquals(expectedEntry.getBasePrice(), responseEntry.getBasePrice());
        Assert.assertEquals(expectedEntry.getQuantity(), responseEntry.getQuantity());
        Assert.assertEquals(expectedEntry.getSequence(), responseEntry.getSequence());
        compareProduct(expectedEntry.getProduct(), responseEntry.getProduct());
    }

    private void compareProduct(ProductDto expectedProduct, ProductDto productResponse) {
        assertEquals(expectedProduct.getId().get(), productResponse.getId().get());
        assertEquals(expectedProduct.getName().get(), productResponse.getName().get());
        assertEquals(expectedProduct.getDescription().get(), productResponse.getDescription().get());
    }

    private List<OrderDto> initOrders() {
        List<OrderDto> orders = new ArrayList<>();
        List<ProductDto> products = initProducts();
        List<OrderEntryPostDto> entries = createEntries(products);
        orders.add(addOrder(new OrderPostDto("test@test.com", entries, Status.APPROVED)).getBody());
        orders.add(addOrder(new OrderPostDto("test2@test.com", entries.subList(0,1), Status.DELIVERED)).getBody());
        orders.add(addOrder(new OrderPostDto("test3@test.com", entries.subList(1,2), Status.PLACED)).getBody());

        //reverse to respect sorting order on return
        Collections.reverse(orders);

        return orders;
    }

    private List<OrderEntryPostDto> createEntries(List<ProductDto> products) {
        List<OrderEntryPostDto> entries = new ArrayList<>();
        int sequence = 0;
        for(ProductDto product : products) {
            entries.add(new OrderEntryPostDto(product.getId().get(), sequence++));
        }
        return entries;
    }

    private ResponseEntity<OrderDto> addOrder(OrderPostDto orderPostDto) {
        ResponseEntity<OrderDto> response = restTemplate.postForEntity(
                baseUrl, orderPostDto, OrderDto.class);
        return response;
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
                baseUrlProduct, product, ProductDto.class);

        return response;
    }


}
