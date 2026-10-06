package com.example.explorer.domain.exception;

public class OrderAccessDeniedException extends BusinessException {
    private final Long orderId;
    private final String userId;

    public OrderAccessDeniedException(Long orderId, String userId) {
        super("ORDER_ACCESS_DENIED", String.format("Access denied to order %d for user %s", orderId, userId));
        this.orderId = orderId;
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }
}
