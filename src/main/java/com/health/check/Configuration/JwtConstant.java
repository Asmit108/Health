package com.health.check.Configuration;

/**
 * Constants used for JWT (JSON Web Token) generation and validation.
 *
 * Centralizes all JWT-related configuration constants to avoid magic strings
 * and make updates easier.
 *
 * @author Health Check Team
 * @version 1.0
 */
public class JwtConstant {
    /** Header name for JWT token in HTTP requests */
    public static String JWT_HEADER = "Authorization";

    /** Header name for user role information in HTTP requests */
    public static String ROLE_HEADER = "Role";

    /** Secret key used for signing and verifying JWT tokens */
    public static String SECRET_KEY = "kjhgeuuuhdfsdbnnjahyutwtyiiolkhuygvbbjdjkdkdkdduidyyytt";

}
