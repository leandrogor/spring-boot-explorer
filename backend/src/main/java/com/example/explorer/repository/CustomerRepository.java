package com.example.explorer.repository;

import com.example.explorer.domain.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    Optional<Customer> findByUserId(String userId);
    Customer save(Customer customer);
    void clear();
}
