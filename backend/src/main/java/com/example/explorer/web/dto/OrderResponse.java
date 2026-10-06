package com.example.explorer.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    Long id,
    Long customerId,
    String customerName,
    String status,
    List<OrderLineResponse> lines,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal total,
    Instant createdAt
) {}
