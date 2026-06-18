package com.health.check.configuration;

import lombok.Getter;

/**
 * Constants used for JWT (JSON Web Token) generation and validation.
 * Centralizes all JWT-related configuration constants to avoid magic strings
 * and make updates easier.
 *
 * @author Health Check Team
 * @version 1.0
 */
@Getter
public class JwtConstant {
    private JwtConstant() {
        /* This utility class should not be instantiated */
    }

    /**
     * Header name for JWT token in HTTP requests
     */
    public static final String JWT_HEADER = "Authorization";

    /**
     * Header name for user role information in HTTP requests
     */
    public static final String ROLE_HEADER = "Role";
}
