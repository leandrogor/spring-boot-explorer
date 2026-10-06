package com.example.explorer.repository.inmemory;

import com.example.explorer.domain.model.Product;
import com.example.explorer.repository.ProductRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("!jpa")
public class InMemoryProductRepository implements ProductRepository {
    private final ConcurrentHashMap<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return store.values().stream()
                .filter(p -> p.getSku().equals(sku))
                .findFirst();
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return store.values().stream()
                .filter(p -> p.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
        }
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return store.values().stream().anyMatch(p -> p.getSku().equals(sku));
    }

    @Override
    public void clear() {
        store.clear();
        idGenerator.set(1);
    }
}
