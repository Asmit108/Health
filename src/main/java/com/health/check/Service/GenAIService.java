package com.health.check.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.health.check.Dto.SymptomResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenAIService {

    @Autowired
    private ChatClient chatClient;

    public SymptomResponseDto checkSymptoms(String symptoms){

        String prompt = """
        A user reports the following symptoms:

        %s

        Respond ONLY in valid JSON with the following fields:

        {
          "possibleCauses": "",
          "severity": "",
          "remedies": "",
          "whenToSeekCare": ""
          "recommendedTests": "",
          "lifeStyleTips": ""
          "typeOfDoctorToSeek": ""
        }

        Do NOT add extra text outside JSON.
        """.formatted(symptoms);

        String aiResponse = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(aiResponse, SymptomResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid AI response: " + aiResponse, e);
        }
    }
}
