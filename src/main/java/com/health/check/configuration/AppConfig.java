package com.health.check.configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * Spring Security and application configuration class.
 * Configures:
 * - JWT-based authentication and authorization
 * - CORS (Cross-Origin Resource Sharing) policies
 * - Security filters for request validation
 * - AI ChatClient for Gen AI integration
 * - Password encryption using BCrypt
 *
 * @author Health Check Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AppConfig {

    private final JwtValidator jwtValidator;

    public AppConfig(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    /**
     * Configures and creates a ChatClient bean for AI service integration.
     * Used to communicate with OpenAI API for symptom analysis.
     *
     * @param chatModel the OpenAiChatModel providing AI capabilities
     * @return ChatClient configured with the provided model
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * Sets up:
     * - Stateless session management (JWT-based, no server sessions)
     * - Public endpoints for authentication and API documentation
     * - JWT validation filter for all other requests
     * - CSRF protection disabled (stateless architecture)
     * - CORS configuration enabled
     *
     * @param http HttpSecurity builder for configuring security
     * @return configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure stateless session management for JWT-based authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Define authorization rules for different endpoints
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to authentication and API documentation
                        .requestMatchers("/auth/**", "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        // Require authentication for all other endpoints
                        .anyRequest().authenticated()
                )

                // Add JWT validation filter before BasicAuthenticationFilter
                .addFilterBefore(jwtValidator, BasicAuthenticationFilter.class)
                // Disable CSRF protection (stateless REST API doesn't need it)
                .csrf(AbstractHttpConfigurer::disable)
                // Disable HTTP Basic authentication (using JWT instead)
                .httpBasic(AbstractHttpConfigurer::disable)
                // Enable CORS with custom configuration
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                );

        return http.build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) policy.
     * Allows the frontend application to make cross-origin requests to this backend.
     *
     * @return CorsConfigurationSource with configured CORS policies
     */
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();

            // Allow requests from frontend (localhost:3000)
            cfg.setAllowedOrigins(List.of("http://localhost:3000")); // no trailing slash

            // Allow standard HTTP methods
            cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));

            // Allow headers needed for authentication and request handling
            cfg.setAllowedHeaders(List.of("Authorization", "Role", "Content-Type"));

            // Don't include credentials in CORS requests
            cfg.setAllowCredentials(false);

            // Cache CORS configuration for 1 hour
            cfg.setMaxAge(3600L);

            return cfg;
        };
    }

    /**
     * Creates a BCryptPasswordEncoder bean for secure password hashing.
     * Used throughout the application to encode and verify user passwords.
     *
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}