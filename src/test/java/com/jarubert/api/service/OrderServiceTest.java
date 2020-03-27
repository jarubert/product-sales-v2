package com.jarubert.api.service;

import com.jarubert.api.model.dto.*;
import com.jarubert.api.model.entity.Order;
import com.jarubert.api.model.entity.OrderEntry;
import com.jarubert.api.model.entity.Price;
import com.jarubert.api.model.entity.Product;
import com.jarubert.api.repository.OrderEntryRepository;
import com.jarubert.api.repository.OrderRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEntryRepository orderEntryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Test
    public void shouldReturnPlacedOrder() {
        OrderPostDto order = new OrderPostDto();
        Product p1 = productRepository.save(new Product("Pipe", "Wood pipe"));
        p1.setPrices(Collections.singletonList(priceRepository.save(new Price(p1, LocalDateTime.now(), null, 15.0))));

        List<OrderEntryPostDto> entries = new ArrayList<>();
        entries.add(new OrderEntryPostDto(p1.getId(), 10));
        order = new OrderPostDto("test@test.com", entries, Status.APPROVED);

        OrderDto placedOrder = orderService.placeOrder(order);

        compareOrder(order, placedOrder);
    }


    @Test
    public void shouldNotReturnAddedOrderOutsideOfRange() {
        createOrder();
        List<OrderDto> returnedOrders =  orderService.getAllInBetween(LocalDateTime.now().plus(2, ChronoUnit.DAYS), LocalDateTime.now().plus(3, ChronoUnit.DAYS));
        Assert.assertEquals(new ArrayList<>(), returnedOrders);
    }

    @Test
    public void shouldReturnAddedOrder() {
        List<OrderDto> returnedOrders =  orderService.getAllInBetween(LocalDateTime.now(), LocalDateTime.now().plus(2, ChronoUnit.DAYS));
        Assert.assertEquals(new ArrayList<>(), returnedOrders);

        Order order = createOrder();

        returnedOrders =  orderService.getAllInBetween(LocalDateTime.now(), LocalDateTime.now().plus(2, ChronoUnit.DAYS));
        Assert.assertEquals(1, returnedOrders.size());

        compareOrder(new OrderDto(order), returnedOrders.get(0));
    }

    private Order createOrder() {
        Product p1 = productRepository.save(new Product("Pipe", "Wood pipe"));
        p1.setPrices(Collections.singletonList(priceRepository.save(new Price(p1, LocalDateTime.now(), null, 150.0))));
        Product p2 = productRepository.save(new Product("Grass", "Smokable grass"));
        p2.setPrices(Collections.singletonList(priceRepository.save(new Price(p2, LocalDateTime.now(), null, 17.0))));
        Order order = orderRepository.save(new Order(LocalDateTime.now().plus(1 , ChronoUnit.DAYS), "teste@teste.com", Status.APPROVED));
        order.getOrderEntries().add(orderEntryRepository.save(new OrderEntry(order, 1, p1, 10)));
        order.getOrderEntries().add(orderEntryRepository.save(new OrderEntry(order, 1, p2, 10)));

        return order;
    }

    private void compareOrder(OrderPostDto expectedOrder, OrderDto responseOrder) {
        Assert.assertEquals(expectedOrder.getBuyer(), responseOrder.getBuyer());
        Assert.assertEquals(expectedOrder.getStatus(), responseOrder.getStatus());
        for (int i = 0; i < expectedOrder.getEntries().size(); i++) {
            compareEntry(expectedOrder.getEntries().get(i), responseOrder.getEntries().get(i));
        }
    }

    private void compareEntry(OrderEntryPostDto expectedEntry, OrderEntryDto responseEntry) {
        Assert.assertEquals(expectedEntry.getProductId(), responseEntry.getProduct().getId().get());
        Assert.assertEquals(expectedEntry.getQuantity(), responseEntry.getQuantity());
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
        assertEquals(expectedProduct.getId(), productResponse.getId());
        assertEquals(expectedProduct.getName(), productResponse.getName());
        assertEquals(expectedProduct.getDescription(), productResponse.getDescription());
    }
}
