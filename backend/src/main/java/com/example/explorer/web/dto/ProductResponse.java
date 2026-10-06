package com.example.explorer.web.dto;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    BigDecimal price,
    int stock,
    Long categoryId,
    String categoryName
) {}
