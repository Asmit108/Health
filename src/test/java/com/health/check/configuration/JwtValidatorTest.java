package com.health.check.configuration;

import com.health.check.models.User;
import com.health.check.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtValidatorTest {

    @InjectMocks
    private JwtValidator jwtValidator;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void swaggerPath_shouldSkipValidation() throws Exception {
        when(request.getServletPath()).thenReturn("/swagger-ui/index.html");

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void v3Path_shouldSkipValidation() throws Exception {
        when(request.getServletPath()).thenReturn("/v3/api-docs");

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void authPath_shouldSkipValidation() throws Exception {
        when(request.getServletPath()).thenReturn("/auth/signup");

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void validJwt_shouldAuthenticateSuccessfully() throws Exception {

        when(request.getServletPath()).thenReturn("/appointments");
        when(request.getHeader(JwtConstant.JWT_HEADER))
                .thenReturn("Bearer validToken");
        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn("PATIENT");

        when(jwtProvider.getEmailFromJwtToken("Bearer validToken"))
                .thenReturn("test@gmail.com");

        User user = new User();
        user.setRole(User.Role.PATIENT);

        when(userService.getUserByEmail("test@gmail.com"))
                .thenReturn(user);

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userService).getUserByEmail("test@gmail.com");

        assertNotNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        assertEquals(
                "test@gmail.com",
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );
    }

    @Test
    void roleMismatch_shouldThrowRuntimeException() throws Exception {

        when(request.getServletPath()).thenReturn("/appointments");
        when(request.getHeader(JwtConstant.JWT_HEADER))
                .thenReturn("Bearer token");
        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn("PATIENT");

        when(jwtProvider.getEmailFromJwtToken("Bearer token"))
                .thenReturn("test@gmail.com");

        User user = new User();
        user.setRole(User.Role.DOCTOR);

        when(userService.getUserByEmail("test@gmail.com"))
                .thenReturn(user);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> jwtValidator.doFilterInternal(request, response, filterChain)
        );

        assertEquals(
                "Role passed in header is wrong",
                ex.getMessage()
        );
    }

    @Test
    void jwtParsingFailure_shouldThrowBadCredentialsException() throws Exception {

        when(request.getServletPath()).thenReturn("/appointments");
        when(request.getHeader(JwtConstant.JWT_HEADER))
                .thenReturn("Bearer token");
        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn("PATIENT");

        when(jwtProvider.getEmailFromJwtToken("Bearer token"))
                .thenThrow(new RuntimeException("JWT parse error"));

        BadCredentialsException ex = assertThrows(
                BadCredentialsException.class,
                () -> jwtValidator.doFilterInternal(request, response, filterChain)
        );

        assertEquals(
                "Invalid JWT token",
                ex.getMessage()
        );
    }
}