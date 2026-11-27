package com.movilidad.ordenes.order.entity;

public enum OrderStatus {
    CREATED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED;

    public boolean canChangeTo(OrderStatus next) {
        return switch (this) {
            case CREATED -> next == IN_TRANSIT || next == CANCELLED;
            case IN_TRANSIT -> next == DELIVERED || next == CANCELLED;
            default -> false;
        };
    }
}