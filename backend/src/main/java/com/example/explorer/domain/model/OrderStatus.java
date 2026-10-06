package com.example.explorer.domain.model;

import java.util.Set;

// En Java, los enums pueden tener campos, métodos y constructores.
// En TypeScript los enums son solo valores; en C# pueden tener extension methods pero no métodos propios.
public enum OrderStatus {
    DRAFT,
    CONFIRMED,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    // Switch expression (Java 14+): más conciso que switch clásico, el compilador verifica exhaustividad
    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case DRAFT      -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED   -> target == PAID || target == CANCELLED;
            case PAID        -> target == SHIPPED || target == CANCELLED;
            case SHIPPED     -> target == DELIVERED; // Ya no se puede cancelar
            case DELIVERED, CANCELLED -> false;      // Estados terminales
        };
    }

    public Set<OrderStatus> validTransitions() {
        return switch (this) {
            case DRAFT      -> Set.of(CONFIRMED, CANCELLED);
            case CONFIRMED   -> Set.of(PAID, CANCELLED);
            case PAID        -> Set.of(SHIPPED, CANCELLED);
            case SHIPPED     -> Set.of(DELIVERED);
            case DELIVERED, CANCELLED -> Set.of();
        };
    }
}
