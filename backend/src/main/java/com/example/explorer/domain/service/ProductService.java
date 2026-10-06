package com.example.explorer.domain.service;

import com.example.explorer.domain.exception.DuplicateSkuException;
import com.example.explorer.domain.model.Category;
import com.example.explorer.domain.model.Product;
import com.example.explorer.repository.CategoryRepository;
import com.example.explorer.repository.ProductRepository;
import com.example.explorer.web.dto.PageResponse;
import com.example.explorer.web.dto.ProductRequest;
import com.example.explorer.web.dto.ProductResponse;
import com.example.explorer.web.mapper.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, 
                          CategoryRepository categoryRepository, 
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    public PageResponse<ProductResponse> findAll(String nameFilter, Long categoryId, int page, int size) {
        List<Product> products = productRepository.findAll();
        
        List<ProductResponse> filtered = products.stream()
            .filter(p -> nameFilter == null || p.getName().toLowerCase().contains(nameFilter.toLowerCase()))
            .filter(p -> categoryId == null || p.getCategoryId().equals(categoryId))
            .map(p -> {
                String catName = categoryRepository.findById(p.getCategoryId())
                                                   .map(Category::getName)
                                                   .orElse("Unknown");
                return productMapper.toResponse(p, catName);
            })
            .toList();

        return PageResponse.of(filtered, page, size);
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
            
        String catName = categoryRepository.findById(product.getCategoryId())
                                           .map(Category::getName)
                                           .orElse("Unknown");
        return productMapper.toResponse(product, catName);
    }

    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
            
        Product entity = productMapper.toEntity(request);
        Product saved = productRepository.save(entity);
        
        return productMapper.toResponse(saved, category.getName());
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
            
        if (!existing.getSku().equals(request.sku()) && productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
            
        productMapper.updateEntity(existing, request);
        Product saved = productRepository.save(existing);
        
        return productMapper.toResponse(saved, category.getName());
    }

    public void delete(Long id) {
        if (productRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        productRepository.deleteById(id);
    }
}
