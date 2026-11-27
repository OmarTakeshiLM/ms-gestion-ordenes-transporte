package com.movilidad.ordenes.driver.dto;

import java.util.UUID;

public record DriverResponse(
        UUID id,
        String name,
        String licenseNumber,
        boolean active) {
}