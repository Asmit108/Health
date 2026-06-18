package com.health.check.controller;

import com.health.check.dto.PatientDto;
import com.health.check.dto.PatientProfileResponse;
import com.health.check.service.PatientService;
import com.health.check.exceptions.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getPatientByIdSuccess() throws Exception {

        Patient patient = new Patient();
        patient.setId(1L);

        when(patientService.getPatientById(1L))
                .thenReturn(patient);

        ResponseEntity<?> response =
                patientController.getPatientById(1L);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                patient,
                response.getBody()
        );

        verify(patientService)
                .getPatientById(1L);
    }

    @Test
    void getPatientByIdNotFound() {

        when(patientService.getPatientById(1L))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> patientController.getPatientById(1L)
        );

        verify(patientService)
                .getPatientById(1L);
    }

    @Test
    void getPatientProfileSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "patient@test.com",
                        null
                )
        );

        PatientProfileResponse profileResponse =
                new PatientProfileResponse();

        when(patientService.getPatientByEmail(
                "patient@test.com"))
                .thenReturn(profileResponse);

        ResponseEntity<?> response =
                patientController.getPatientProfile();

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                profileResponse,
                response.getBody()
        );

        verify(patientService)
                .getPatientByEmail(
                        "patient@test.com"
                );
    }

    @Test
    void updatePatientSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "patient@test.com",
                        null
                )
        );

        PatientDto request = new PatientDto();

        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);

        when(patientService.updatePatient(
                "patient@test.com",
                request))
                .thenReturn(updatedPatient);

        ResponseEntity<?> response =
                patientController.updatePatient(
                        request
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                updatedPatient,
                response.getBody()
        );

        verify(patientService)
                .updatePatient(
                        "patient@test.com",
                        request
                );
    }

    @Test
    void deletePatientSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "patient@test.com",
                        null
                )
        );

        Patient patient = new Patient();
        patient.setId(10L);

        PatientProfileResponse profileResponse =
                new PatientProfileResponse();
        profileResponse.setPatient(patient);

        when(patientService.getPatientByEmail(
                "patient@test.com"))
                .thenReturn(profileResponse);

        ResponseEntity<String> response =
                patientController.deletePatient();

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                "Patient deleted successfully",
                response.getBody()
        );

        verify(patientService)
                .getPatientByEmail(
                        "patient@test.com"
                );

        verify(patientService)
                .deletePatient(10L);
    }
}
