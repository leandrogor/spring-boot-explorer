package com.example.explorer.web.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
    Long productId,
    String productSku,
    String productName,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal
) {}
