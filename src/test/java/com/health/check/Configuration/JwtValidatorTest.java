package com.health.check.Configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtValidatorTest {

    @InjectMocks
    private JwtValidator filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    // ---------------- Swagger bypass ----------------

    @Test
    void swaggerPath_shouldBypassFilter() throws Exception {
        when(request.getServletPath()).thenReturn("/swagger-ui/index.html");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    // ---------------- v3 bypass ----------------

    @Test
    void v3Path_shouldBypassFilter() throws Exception {
        when(request.getServletPath()).thenReturn("/v3/api-docs");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    // ---------------- Missing JWT ----------------

    @Test
    void missingJwt_shouldThrowBadCredentials() {
        when(request.getServletPath()).thenReturn("/api/test");
        when(request.getHeader(JwtConstant.ROLE_HEADER)).thenReturn(null);
        when(request.getHeader(JwtConstant.JWT_HEADER)).thenReturn(null);

        assertThrows(BadCredentialsException.class,
                () -> filter.doFilterInternal(request, response, filterChain));
    }

    // ---------------- Invalid JWT format ----------------

    @Test
    void invalidJwt_shouldThrowBadCredentials() {
        when(request.getServletPath()).thenReturn("/api/test");
        when(request.getHeader(JwtConstant.ROLE_HEADER)).thenReturn(null);
        when(request.getHeader(JwtConstant.JWT_HEADER)).thenReturn("InvalidToken");

        assertThrows(BadCredentialsException.class,
                () -> filter.doFilterInternal(request, response, filterChain));
    }

    // ---------------- Auth path role check ----------------

    @Test
    void authPath_Role_shouldThrowAccessDenied() {
        when(request.getServletPath()).thenReturn("/auth/signup");
        when(request.getHeader(JwtConstant.ROLE_HEADER)).thenReturn("ADMIN");

        assertThrows(AccessDeniedException.class,
                () -> filter.doFilterInternal(request, response, filterChain));
    }

    @Test
    void authPath_PatientRole() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/auth/signup");
        when(request.getHeader(JwtConstant.ROLE_HEADER)).thenReturn("PATIENT");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void authPath_DoctorRole() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/auth/signup");
        when(request.getHeader(JwtConstant.ROLE_HEADER)).thenReturn("DOCTOR");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    // ---------------- Valid JWT flow ----------------

    @Test
    void validJwt_shouldSetAuthentication() throws Exception {

        when(request.getServletPath()).thenReturn("/api/test");
        when(request.getHeader(JwtConstant.JWT_HEADER)).thenReturn("Bearer validToken");
        when(request.getHeader(JwtConstant.ROLE_HEADER)).thenReturn("PATIENT");

        try (MockedStatic<JwtProvider> mocked = mockStatic(JwtProvider.class)) {

            mocked.when(() -> JwtProvider.getEmailFromJwtToken("Bearer validToken"))
                    .thenReturn("test@gmail.com");

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);

            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }
}