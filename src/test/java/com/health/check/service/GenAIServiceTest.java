package com.health.check.service;

import com.health.check.dto.SymptomResponseDto;
import com.health.check.exceptions.ResponseParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenAIServiceTest {

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private GenAIService genAIService;

    @Test
    void checkSymptoms_Success() {

        String fakeJson = """
                {
                  "possibleCauses": "flu",
                  "severity": "low",
                  "remedies": "rest",
                  "whenToSeekCare": "if worsens",
                  "recommendedTests": "none",
                  "lifeStyleTips": "sleep well",
                  "typeOfDoctorToSeek": "general"
                }
                """;

        // Mock chain: prompt().user().call().content()
        ChatClient.ChatClientRequestSpec request = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.ChatClientRequestSpec afterUser = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponse = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(request);
        when(request.user(anyString())).thenReturn(afterUser);
        when(afterUser.call()).thenReturn(callResponse);
        when(callResponse.content()).thenReturn(fakeJson);

        SymptomResponseDto result =
                genAIService.checkSymptoms("fever and cold");

        assertNotNull(result);
    }

    @Test
    void checkSymptoms_InvalidJson_ThrowsException() {

        String invalidJson = "NOT VALID JSON";

        ChatClient.ChatClientRequestSpec request = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.ChatClientRequestSpec afterUser = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec callResponse = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(request);
        when(request.user(anyString())).thenReturn(afterUser);
        when(afterUser.call()).thenReturn(callResponse);
        when(callResponse.content()).thenReturn(invalidJson);

        assertThrows(ResponseParseException.class,
                () -> genAIService.checkSymptoms("fever"));
    }
}