package com.jarubert.api.repository;

import com.jarubert.api.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by jarubert on 2020-03-24.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    public List<Order> findByDateBetweenOrderByIdDesc(LocalDateTime startDate, LocalDateTime endDate);
}
