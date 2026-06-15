package com.health.check.Controller;

import com.health.check.Dto.PatientProfileResponse;
import com.health.check.Dto.SymptomRequestDto;
import com.health.check.Dto.SymptomResponseDto;
import com.health.check.Service.GenAIService;
import com.health.check.Service.PatientService;
import com.health.check.Service.SymptomService;
import com.health.check.models.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenAIControllerTest {

    @Mock
    private GenAIService genAIService;

    @Mock
    private PatientService patientService;

    @Mock
    private SymptomService symptomService;

    @InjectMocks
    private GenAIController genAIController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkSymptomsSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "patient@test.com",
                        null
                )
        );

        SymptomRequestDto request = new SymptomRequestDto();
        request.setSymptoms(
                List.of("fever", "cough").toString()
        );

        Patient patient = new Patient();
        patient.setId(1L);

        PatientProfileResponse patientProfileResponse =
                new PatientProfileResponse();
        patientProfileResponse.setPatient(patient);

        SymptomResponseDto responseDto =
                new SymptomResponseDto();

        when(patientService.getPatientByEmail(
                "patient@test.com"))
                .thenReturn(patientProfileResponse);

        when(genAIService.checkSymptoms(
                request.getSymptoms()))
                .thenReturn(responseDto);

        ResponseEntity<?> response =
                genAIController.check(
                        request
                );

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertEquals(
                responseDto,
                response.getBody()
        );

        verify(patientService)
                .getPatientByEmail(
                        "patient@test.com"
                );

        verify(genAIService)
                .checkSymptoms(
                        request.getSymptoms()
                );

        verify(symptomService)
                .createSymptoms(
                        patient,
                        responseDto
                );
    }
}
