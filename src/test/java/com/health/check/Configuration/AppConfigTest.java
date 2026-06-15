package com.health.check.Configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppConfigTest {

    @InjectMocks
    private AppConfig appConfig;

    @Mock
    private OpenAiChatModel chatModel;

    @Test
    void chatClient_shouldReturnBean() {
        ChatClient client = appConfig.chatClient(chatModel);

        assertNotNull(client);
    }

    @Test
    void passwordEncoder_shouldReturnBean() {
        BCryptPasswordEncoder encoder = appConfig.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder.matches("123", encoder.encode("123")));
    }

    @Test
    void corsConfigurationSource_shouldReturnValidConfig() {
        CorsConfigurationSource source = appConfig.corsConfigurationSource();

        CorsConfiguration config = source.getCorsConfiguration(null);

        assertNotNull(config);
        assertEquals(List.of("http://localhost:3000"), config.getAllowedOrigins());
        assertNotNull(config.getAllowedMethods());
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertNotNull(config.getAllowedHeaders());
        assertTrue(config.getAllowedHeaders().contains("Authorization"));
    }

    @Test
    void securityFilterChain_shouldBuildSuccessfully() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        SecurityFilterChain chain = appConfig.securityFilterChain(http);

        assertNotNull(chain);
    }
}