package com.jarubert.api.util;

import com.jarubert.api.repository.OrderRepository;
import com.jarubert.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RollbackUtil {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public void rollbackProducts() {
        productRepository.deleteAll();
        productRepository.flush();
    }

    public void rollbackOrders() {
        orderRepository.deleteAll();
        orderRepository.flush();
    }
}
