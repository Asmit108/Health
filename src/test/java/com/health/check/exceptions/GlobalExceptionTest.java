package com.health.check.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionTest {

    private final GlobalException globalException = new GlobalException();

    @Test
    void accessDeniedExceptionTest() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL())
                .thenReturn(new StringBuffer("/patients"));

        ResponseEntity<ErrorDetails> response =
                globalException.AccessDeniedException(
                        new AccessDeniedException("Access denied"),
                        request
                );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Access denied",
                response.getBody().getMessage()
        );
    }

    @Test
    void badCredentialsExceptionTest() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL())
                .thenReturn(new StringBuffer("/auth/signin"));

        ResponseEntity<ErrorDetails> response =
                globalException.BadCredentialsException(
                        new BadCredentialsException("Wrong password"),
                        request
                );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Wrong password",
                response.getBody().getMessage()
        );
    }

    @Test
    void alreadyExistsExceptionTest() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL())
                .thenReturn(new StringBuffer("/auth/signup"));

        ResponseEntity<ErrorDetails> response =
                globalException.AlreadyExistsException(
                        new AlreadyExistsException("User already exists"),
                        request
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "User already exists",
                response.getBody().getMessage()
        );
    }

    @Test
    void notFoundExceptionTest() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL())
                .thenReturn(new StringBuffer("/patients/1"));

        ResponseEntity<ErrorDetails> response =
                globalException.NotFoundException(
                        new NotFoundException("Patient not found"),
                        request
                );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "Patient not found",
                response.getBody().getMessage()
        );
    }

    @Test
    void responseParseExceptionTest() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL())
                .thenReturn(new StringBuffer("/symptoms/check"));

        ResponseEntity<ErrorDetails> response =
                globalException.ResponseParseAndOtherException(
                        new ResponseParseException("Invalid AI response"),
                        request
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());
        assertEquals(
                "Invalid AI response",
                response.getBody().getMessage()
        );
    }
}