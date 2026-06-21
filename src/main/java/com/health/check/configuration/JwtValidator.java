package com.health.check.configuration;

import com.health.check.models.User;
import com.health.check.service.UserService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * JWT validation filter that validates JWT tokens in HTTP requests.
 * Executed once per request to:
 * - Extract and validate JWT tokens from Authorization header
 * - Extract user email and role information from JWT
 * - Set authentication context for the request
 * - Allow public endpoints (auth, swagger) without token validation
 *
 * @author Health Check Team
 * @version 1.0
 */
@Component
public class JwtValidator extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userService;
    public JwtValidator(JwtProvider jwtProvider,  UserService userService) {
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }
    /**
     * Executes JWT validation logic for each incoming request.
     * Validates JWT token and sets up security context with user authentication.
     * Allows unauthenticated access to public endpoints.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain to continue request processing
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws BadCredentialsException if JWT token is invalid or missing
     * @throws AccessDeniedException if role validation fails
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        // Skip JWT validation for Swagger/API documentation endpoints
        if (path.contains("/swagger-ui") || path.contains("/v3")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract role from request header
        String role = request.getHeader(JwtConstant.ROLE_HEADER);

        // For authentication endpoints, validate that role is either PATIENT or DOCTOR
        if (path.contains("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        // Validate JWT token exists and has correct format
        if (jwt == null || !jwt.startsWith("Bearer ")) {
            throw new BadCredentialsException("Invalid or missing JWT token");
        }

        try {
            // Extract email from JWT token
            String email = jwtProvider.getEmailFromJwtToken(jwt);
            User user = userService.getUserByEmail(email);
            if(!Objects.equals(user.getRole().toString(), role)) {
                throw new Exception("Role passed in header is wrong");
            }
            // Create authorities list with user role
            Collection<? extends GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            // Create authentication object with email and role
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            // Set authentication in security context for this request
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            if ("Role passed in header is wrong".equals(e.getMessage())) {
                throw new RuntimeException("Role passed in header is wrong");
            }
            else{
                throw new BadCredentialsException("Invalid JWT token");
            }
        }

        // Continue the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
