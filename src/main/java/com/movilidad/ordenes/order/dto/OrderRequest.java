package com.movilidad.ordenes.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderRequest(
        @NotBlank(message = "El origen es obligatorio")
        @Size(max = 150, message = "El origen no puede pasar de 150 caracteres")
        String origin,

        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 150, message = "El destino no puede pasar de 150 caracteres")
        String destination) {
}