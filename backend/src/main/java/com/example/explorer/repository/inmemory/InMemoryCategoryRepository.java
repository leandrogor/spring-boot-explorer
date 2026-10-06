package com.example.explorer.repository.inmemory;

import com.example.explorer.domain.model.Category;
import com.example.explorer.repository.CategoryRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("!jpa")
public class InMemoryCategoryRepository implements CategoryRepository {
    private final ConcurrentHashMap<Long, Category> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }
    
    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(idGenerator.getAndIncrement());
        }
        store.put(category.getId(), category);
        return category;
    }

    @Override
    public void clear() {
        store.clear();
        idGenerator.set(1);
    }
}
