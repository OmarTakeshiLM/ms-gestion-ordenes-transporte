package com.movilidad.ordenes.order.dto;

import com.movilidad.ordenes.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        OrderStatus status,
        String origin,
        String destination,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}