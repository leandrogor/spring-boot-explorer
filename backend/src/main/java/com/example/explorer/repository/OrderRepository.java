package com.example.explorer.repository;

import com.example.explorer.domain.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<Order> findAll();
    Optional<Order> findById(Long id);
    List<Order> findByCustomerId(Long customerId);
    Order save(Order order);
    void clear();
}
