package com.example.explorer.domain.event;

import java.math.BigDecimal;
import java.time.Instant;

// Record (Java 16+): clase inmutable con constructor, getters, equals, hashCode y toString auto-generados.
// Equivale a un type/interface en TS o a un record en C#.
public record OrderConfirmedEvent(
    Long orderId,
    Long customerId,
    BigDecimal total,
    Instant timestamp
) {}
