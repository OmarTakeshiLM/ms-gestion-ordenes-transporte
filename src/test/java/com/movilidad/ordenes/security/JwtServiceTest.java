package com.movilidad.ordenes.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService =
            new JwtService("clave-de-prueba-para-tests-con-al-menos-32-caracteres", 60_000);

    @Test
    void generatesAndReadsToken() {
        String token = jwtService.generateToken("admin");

        assertEquals("admin", jwtService.extractUsername(token));
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        JwtService attacker = new JwtService("otra-clave-distinta-que-no-es-la-del-servidor-123", 60_000);
        String token = attacker.generateToken("admin");

        assertThrows(JwtException.class, () -> jwtService.extractUsername(token));
    }

    @Test
    void rejectsExpiredToken() {
        JwtService expired = new JwtService("clave-de-prueba-para-tests-con-al-menos-32-caracteres", -1000);
        String token = expired.generateToken("admin");

        assertThrows(JwtException.class, () -> expired.extractUsername(token));
    }

    @Test
    void rejectsTokenWithModifiedPayload() {
        String[] parts = jwtService.generateToken("admin").split("\\.");
        String fakePayload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"hacker\"}".getBytes());
        String token = parts[0] + "." + fakePayload + "." + parts[2];

        assertThrows(JwtException.class, () -> jwtService.extractUsername(token));
    }
}