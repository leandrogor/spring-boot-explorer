package com.example.explorer.repository.inmemory;

import com.example.explorer.domain.model.Customer;
import com.example.explorer.repository.CustomerRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("!jpa")
public class InMemoryCustomerRepository implements CustomerRepository {
    private final ConcurrentHashMap<Long, Customer> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Customer> findByUserId(String userId) {
        return store.values().stream()
                .filter(c -> c.getUserId().equals(userId))
                .findFirst();
    }
    
    @Override
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(idGenerator.getAndIncrement());
        }
        store.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public void clear() {
        store.clear();
        idGenerator.set(1);
    }
}
