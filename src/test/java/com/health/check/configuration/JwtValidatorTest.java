package com.health.check.configuration;

import com.health.check.exceptions.NotFoundException;
import com.health.check.models.User;
import com.health.check.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtValidatorTest {

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

    @Mock
    private PrintWriter writer;

    private JwtValidator jwtValidator;

    @BeforeEach
    void setUp() {
        jwtValidator = new JwtValidator(jwtProvider, userService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowSwaggerUiRequest() throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/swagger-ui/index.html");

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtProvider, userService);
    }

    @Test
    void shouldAllowApiDocsRequest() throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/v3/api-docs");

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtProvider, userService);
    }

    @Test
    void shouldAllowAuthRequest() throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/api/auth/login");

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtProvider, userService);
    }

    @Test
    void shouldReturnUnauthorizedWhenRoleHeaderIsMissing()
            throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/api/users");

        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn(null);

        when(response.getWriter())
                .thenReturn(writer);

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(writer)
                .write("Role header is missing");

        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedWhenRoleIsInvalid()
            throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/api/users");

        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn("ADMIN");

        when(response.getWriter())
                .thenReturn(writer);

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(writer)
                .write("Invalid role");

        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    void shouldAllowAuthPathAfterValidRole()
            throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/something/auth/test");

        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn(User.Role.PATIENT.name());

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtProvider, userService);
    }

    @Test
    void shouldReturnUnauthorizedWhenJwtIsMissing()
            throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/api/users");

        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn(User.Role.PATIENT.name());

        when(request.getHeader(JwtConstant.JWT_HEADER))
                .thenReturn(null);

        when(response.getWriter())
                .thenReturn(writer);

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(writer)
                .write("Invalid or missing JWT token");

        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedWhenJwtDoesNotStartWithBearer()
            throws ServletException, IOException {

        when(request.getServletPath())
                .thenReturn("/api/users");

        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn(User.Role.PATIENT.name());

        when(request.getHeader(JwtConstant.JWT_HEADER))
                .thenReturn("InvalidToken");

        when(response.getWriter())
                .thenReturn(writer);

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(writer)
                .write("Invalid or missing JWT token");

        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedWhenUserDoesNotExist()
            throws ServletException, IOException, NotFoundException {

        String jwt = "Bearer valid-token";
        String email = "test@example.com";

        when(request.getServletPath())
                .thenReturn("/api/users");

        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn(User.Role.PATIENT.name());

        when(request.getHeader(JwtConstant.JWT_HEADER))
                .thenReturn(jwt);

        when(jwtProvider.getEmailFromJwtToken(jwt))
                .thenReturn(email);

        when(userService.getUserByEmail(email))
                .thenThrow(new NotFoundException("User not found"));

        when(response.getWriter())
                .thenReturn(writer);

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(writer)
                .write("User not found");
    }

    @Test
    void shouldReturnUnauthorizedWhenRoleDoesNotMatchUserRole()
            throws ServletException, IOException, NotFoundException {

        String jwt = "Bearer valid-token";
        String email = "test@example.com";

        User user = mock(User.class);

        when(request.getServletPath())
                .thenReturn("/api/users");

        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn(User.Role.PATIENT.name());

        when(request.getHeader(JwtConstant.JWT_HEADER))
                .thenReturn(jwt);

        when(jwtProvider.getEmailFromJwtToken(jwt))
                .thenReturn(email);

        when(userService.getUserByEmail(email))
                .thenReturn(user);

        when(user.getRole())
                .thenReturn(User.Role.DOCTOR);

        when(response.getWriter())
                .thenReturn(writer);

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(response)
                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        verify(writer)
                .write("Role passed in header is wrong");

        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    void shouldAuthenticateWhenJwtAndRoleAreValid()
            throws ServletException, IOException, NotFoundException {

        String jwt = "Bearer valid-token";
        String email = "test@example.com";

        User user = mock(User.class);

        when(request.getServletPath())
                .thenReturn("/api/users");

        when(request.getHeader(JwtConstant.ROLE_HEADER))
                .thenReturn(User.Role.PATIENT.name());

        when(request.getHeader(JwtConstant.JWT_HEADER))
                .thenReturn(jwt);

        when(jwtProvider.getEmailFromJwtToken(jwt))
                .thenReturn(email);

        when(userService.getUserByEmail(email))
                .thenReturn(user);

        when(user.getRole())
                .thenReturn(User.Role.PATIENT);

        jwtValidator.doFilterInternal(request, response, filterChain);

        verify(filterChain)
                .doFilter(request, response);

        assertEquals(
                email,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
        );

        assertEquals(
                "ROLE_PATIENT",
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );
    }
}