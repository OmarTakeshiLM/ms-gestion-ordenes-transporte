package com.movilidad.ordenes.order.dto;

import com.movilidad.ordenes.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @NotNull(message = "El estado es obligatorio")
        OrderStatus status) {
}