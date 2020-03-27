package com.jarubert.api.repository;

import com.jarubert.api.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by jarubert on 2020-03-24.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    public List<Product> findAllByOrderByIdDesc();

    public Optional<Product> findOneByName(String name);
}
