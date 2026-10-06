package com.example.explorer.web.mapper;

import com.example.explorer.domain.model.Product;
import com.example.explorer.web.dto.ProductRequest;
import com.example.explorer.web.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product, String categoryName) {
        return new ProductResponse(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getPrice(),
            product.getStock(),
            product.getCategoryId(),
            categoryName
        );
    }

    public Product toEntity(ProductRequest request) {
        return new Product(
            null,
            request.sku(),
            request.name(),
            request.price(),
            request.stock(),
            request.categoryId()
        );
    }

    public void updateEntity(Product product, ProductRequest request) {
        product.setSku(request.sku());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategoryId(request.categoryId());
    }
}
