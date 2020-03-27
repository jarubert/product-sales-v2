package com.jarubert.api.service;

import com.jarubert.api.model.dto.OrderDto;
import com.jarubert.api.model.dto.OrderEntryDto;
import com.jarubert.api.model.dto.OrderEntryPostDto;
import com.jarubert.api.model.dto.OrderPostDto;
import com.jarubert.api.model.entity.Order;
import com.jarubert.api.model.entity.OrderEntry;
import com.jarubert.api.model.entity.Product;
import com.jarubert.api.repository.OrderEntryRepository;
import com.jarubert.api.repository.OrderRepository;
import com.jarubert.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarubert on 2020-03-24.
 */
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderEntryRepository orderEntryRepository;
    @Autowired
    private ProductRepository productRepository;

    public List<OrderDto> getAllInBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByDateBetweenOrderByIdDesc(startDate, endDate)
                .stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
    }

    public OrderDto placeOrder(OrderPostDto newOrder) {
        Order placedOrder = orderRepository.save(new Order(newOrder));
        List<OrderEntryDto> addedEntries = insertEntries(placedOrder, newOrder);

        OrderDto placedOrderResponse = new OrderDto(placedOrder);
        placedOrderResponse.setEntries(addedEntries);
        placedOrderResponse.calculateTotalPrice();
        return placedOrderResponse;
    }

    private List<OrderEntryDto> insertEntries(Order placedOrder, OrderPostDto newOrder) {
        List<OrderEntryDto> addedEntries = new ArrayList<>();
        int sequence = 1;
        for (OrderEntryPostDto entry : newOrder.getEntries()) {
            Product product = productRepository.getOne(entry.getProductId());
            addedEntries.add(new OrderEntryDto(orderEntryRepository.save(new OrderEntry(placedOrder, sequence++, product, entry.getQuantity())), placedOrder.getDate()));
        }

        return addedEntries;
    }
}
