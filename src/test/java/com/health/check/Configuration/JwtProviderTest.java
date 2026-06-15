package com.health.check.Configuration;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    @Test
    void generateToken_shouldReturnValidToken() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken("test@mail.com", null);

        String token = JwtProvider.generateToken(auth);

        assertNotNull(token);
        assertTrue(token.length() > 10);
    }

    @Test
    void getEmailFromJwtToken_shouldReturnEmail() {
        Authentication auth =
                new UsernamePasswordAuthenticationToken("test@mail.com", null);

        String token = JwtProvider.generateToken(auth);

        String result = JwtProvider.getEmailFromJwtToken("Bearer " + token);

        assertEquals("test@mail.com", result);
    }

    @Test
    void getEmailFromJwtToken_invalidPrefix_shouldThrowException() {
        String invalidToken = "invalidToken";

        assertThrows(Exception.class,
                () -> JwtProvider.getEmailFromJwtToken(invalidToken));
    }

    @Test
    void getEmailFromJwtToken_malformedToken_shouldThrowException() {
        assertThrows(Exception.class,
                () -> JwtProvider.getEmailFromJwtToken("Bearer abc.def.ghi"));
    }
}