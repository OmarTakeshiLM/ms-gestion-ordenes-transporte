package com.movilidad.ordenes.assignment.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignmentRequest(
        @NotNull(message = "El id de la orden es obligatorio")
        UUID orderId,

        @NotNull(message = "El id del conductor es obligatorio")
        UUID driverId
) {
}