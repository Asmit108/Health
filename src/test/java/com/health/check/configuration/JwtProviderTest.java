package com.health.check.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String secret =
                "abcdefghijklmnopqrstuvwxyz12345678901234567890";
        jwtProvider = new JwtProvider(secret);
    }

    @Test
    void constructor_shouldCreateInstance() {
        assertNotNull(jwtProvider);
    }

    @Test
    void generateToken_shouldReturnValidJwt() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "test@example.com",
                        null
                );

        String token = jwtProvider.generateToken(auth);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getEmailFromJwtToken_shouldReturnEmail() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "test@example.com",
                        null
                );

        String token = jwtProvider.generateToken(auth);

        String email =
                jwtProvider.getEmailFromJwtToken(
                        "Bearer " + token
                );

        assertEquals("test@example.com", email);
    }

    @Test
    void getEmailFromJwtToken_shouldThrowExceptionForInvalidToken() {
        assertThrows(
                Exception.class,
                () -> jwtProvider.getEmailFromJwtToken(
                        "Bearer invalid-token"
                )
        );
    }
}