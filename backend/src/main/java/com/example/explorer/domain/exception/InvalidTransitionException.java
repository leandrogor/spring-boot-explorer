package com.example.explorer.domain.exception;

import com.example.explorer.domain.model.OrderStatus;

public class InvalidTransitionException extends BusinessException {
    private final OrderStatus currentStatus;
    private final OrderStatus targetStatus;

    public InvalidTransitionException(OrderStatus currentStatus, OrderStatus targetStatus) {
        super("INVALID_TRANSITION", String.format("Cannot transition from %s to %s", currentStatus, targetStatus));
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public OrderStatus getTargetStatus() {
        return targetStatus;
    }
}
