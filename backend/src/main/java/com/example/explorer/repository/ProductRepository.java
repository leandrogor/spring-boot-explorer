package com.example.explorer.repository;

import com.example.explorer.domain.model.Product;
import java.util.List;
import java.util.Optional;

// Interfaz que define el contrato de acceso a datos.
// En Express/TS harías una interfaz o type. En .NET sería IProductRepository.
// La implementación concreta (in-memory o JPA) se inyecta por DI.
public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Optional<Product> findBySku(String sku);
    List<Product> findByCategoryId(Long categoryId);
    Product save(Product product);
    void deleteById(Long id);
    boolean existsBySku(String sku);
    void clear();
}
