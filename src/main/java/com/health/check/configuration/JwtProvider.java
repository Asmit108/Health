package com.health.check.configuration;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utility class for JWT (JSON Web Token) generation and validation.
 * Handles creating new JWT tokens for authenticated users and extracting
 * user information from existing tokens.
 *
 * @author Health Check Team
 * @version 1.0
 */
@Component
public class JwtProvider {
    /* Secret key used for HMAC-SHA signing of JWT tokens */
    /**
     * Secret key used for signing and verifying JWT tokens
     */
    private final SecretKey key;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    /**
     * Generates a new JWT token for an authenticated user.
     * Creates a signed JWT token containing the user's email address
     * and other claims. Token expires after 24 hours.
     *
     * @param auth the Authentication object containing user details and authorities
     * @return a signed JWT token string (without Bearer prefix)
     */
    public String generateToken(Authentication auth) {
        // Build and sign JWT token with user information
        return Jwts.builder()
                // Set token issuer
                .setIssuer("social")
                // Set time token was issued
                .setIssuedAt(new Date())
                // Set token expiration to 24 hours from now (86400000 milliseconds)
                .setExpiration(new Date(new Date().getTime() + 86400000))
                // Store user email as a claim for later retrieval
                .claim("email", auth.getName())
                // Sign with secret key
                .signWith(key)
                // Compact the JWT to a URL-safe string
                .compact();
    }

    /**
     * Extracts the email address from a JWT token.
     * Parses and validates the JWT token, extracting the email claim.
     * Expects token in "Bearer <token>" format.
     *
     * @param jwt the JWT token string with "Bearer " prefix
     * @return the email address stored in the token
     */
    public String getEmailFromJwtToken(String jwt) {
        // Remove "Bearer " prefix (7 characters)
        jwt = jwt.substring(7);

        // Parse JWT and extract claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        // Retrieve and return email from claims
        return String.valueOf(claims.get("email"));
    }
}
