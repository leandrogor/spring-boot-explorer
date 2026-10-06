package com.example.explorer.domain.model;

import com.example.explorer.domain.exception.InsufficientStockException;
import java.math.BigDecimal;

public class Product {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private int stock;
    private Long categoryId;

    public Product() {
    }

    public Product(Long id, String sku, String name, BigDecimal price, int stock, Long categoryId) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
    }

    public void reserveStock(int quantity) {
        if (this.stock < quantity) {
            throw new InsufficientStockException(this.id, this.stock, quantity);
        }
        this.stock -= quantity;
    }

    public void restoreStock(int quantity) {
        this.stock += quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
