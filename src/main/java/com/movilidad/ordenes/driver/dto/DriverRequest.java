package com.movilidad.ordenes.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DriverRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede pasar de 120 caracteres")
        String name,

        @NotBlank(message = "El número de licencia es obligatorio")
        @Size(max = 30, message = "El número de licencia no puede pasar de 30 caracteres")
        String licenseNumber,
        Boolean active) {
}