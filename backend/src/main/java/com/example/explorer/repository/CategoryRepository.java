package com.example.explorer.repository;

import com.example.explorer.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    boolean existsById(Long id);
    Category save(Category category);
    void clear();
}
