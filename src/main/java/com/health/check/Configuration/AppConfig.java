package com.health.check.Configuration;

import com.health.check.Repository.UserRepository;
import com.health.check.models.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class AppConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean//OpenAiChatModel object is injected as bean from the application context
    // if this is not there,springboot not create bean, it is like simple pojo class
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(management->management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(Authorize -> Authorize
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")//is checked in securitycontextholder
                        .requestMatchers("/api/**").authenticated()// is checked in ""
                        .anyRequest().permitAll())
                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {

                            // Extract email from Google OAuth2
                            String email = authentication.getName();
                            if (userRepository.findByEmail(email) == null) {
                                User user = new User();
                                user.setEmail(email);
                                userRepository.save(user);
                            }
                            // Create fake Authentication object for JwtProvider
                            Authentication fakeAuth =
                                    new UsernamePasswordAuthenticationToken(email, null);

                            // Generate JWT with your existing class
                            String jwt = JwtProvider.generateToken(fakeAuth);

                            // Return JWT to frontend
                            response.setContentType("application/json");
                            response.getWriter().write("{\"token\":\"" + jwt + "\"}");
                        })
                )

                .addFilterBefore(new jwtValidator(), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .cors(cors->cors.configurationSource(corsConfigurationSource()));//lambda expression
        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {

        return new CorsConfigurationSource() {//object of anonymous class implementing interface
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration cfg = new CorsConfiguration();
                cfg.setAllowedOrigins(Arrays.asList("http://localhost:3000/"));
                cfg.setAllowedMethods(Collections.singletonList("*"));
                cfg.setAllowCredentials(true);//allow client to send receive crdentials
                cfg.setAllowedHeaders(Collections.singletonList("*"));// allow client to send particular header
                cfg.setExposedHeaders(Arrays.asList("Authorization"));//allow client to access authorization header
                cfg.setMaxAge(3600L);
//                CORS request to a server, it first sends an OPTIONS request to check whether the actual request is
//                allowed. The pre-flight request is used to check if the server will accept the request based on its
//                CORS configuration (e.g., allowed headers, methods, etc.).
//
//                The maxAge defines how long the browser can cache the results of the pre-flight request before
//                it has to send another pre-flight (OPTIONS) request.
//
//                3600L: This means the browser will cache the pre-flight response for 3600 seconds (1 hour).
//                    During this time, the client doesn't need to send a new OPTIONS request every time it makes a
//                request to the server, as long as the cache is valid.
                return cfg;
            }
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}