package com.jarubert.api.repository;

import com.jarubert.api.model.entity.Price;
import com.jarubert.api.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by jarubert on 2020-03-24.
 */
public interface PriceRepository extends JpaRepository<Price, Long> {
}
