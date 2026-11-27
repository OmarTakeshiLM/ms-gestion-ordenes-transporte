package com.movilidad.ordenes.assignment.dto;

import com.movilidad.ordenes.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentResponse(
        UUID id,
        UUID orderId,
        OrderStatus orderStatus,
        UUID driverId,
        String driverName,
        LocalDateTime assignedAt,
        String documentName,
        String imageName
) {
}