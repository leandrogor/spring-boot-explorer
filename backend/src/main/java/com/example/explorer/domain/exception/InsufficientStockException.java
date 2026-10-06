package com.example.explorer.domain.exception;

public class InsufficientStockException extends BusinessException {
    private final Long productId;
    private final int available;
    private final int requested;

    public InsufficientStockException(Long productId, int available, int requested) {
        super("INSUFFICIENT_STOCK", String.format("Insufficient stock for product %d. Available: %d, Requested: %d", productId, available, requested));
        this.productId = productId;
        this.available = available;
        this.requested = requested;
    }

    public Long getProductId() {
        return productId;
    }

    public int getAvailable() {
        return available;
    }

    public int getRequested() {
        return requested;
    }
}
