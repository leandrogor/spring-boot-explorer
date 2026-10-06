package com.example.explorer.domain.model;

import com.example.explorer.domain.exception.InvalidTransitionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class Order {
    private Long id;
    private Long customerId;
    private OrderStatus status;
    private List<OrderLine> lines;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private Instant createdAt;

    public Order(Long customerId, List<OrderLine> lines) {
        this.customerId = customerId;
        this.lines = lines;
        this.status = OrderStatus.DRAFT;
        this.createdAt = Instant.now();
        this.subtotal = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    public void transitionTo(OrderStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new InvalidTransitionException(this.status, target);
        }
        this.status = target;
    }

    public void applyPricing(BigDecimal discountRate) {
        this.subtotal = this.lines.stream()
                .map(OrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.discount = this.subtotal.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
        this.total = this.subtotal.subtract(this.discount);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
