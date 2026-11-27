package com.movilidad.ordenes.security.dto;

public record TokenResponse(
        String token,
        String type,
        long expiresIn
) {
}