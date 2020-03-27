package com.jarubert.api.repository;

import com.jarubert.api.model.entity.OrderEntry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jarubert on 2020-03-24.
 */
public interface OrderEntryRepository extends JpaRepository<OrderEntry, Long> {

}
